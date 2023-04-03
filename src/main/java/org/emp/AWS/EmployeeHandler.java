package org.emp.AWS;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;
import org.emp.AWS.payrollSystem.Employee;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;


import java.io.*;
import java.util.Map;

public class EmployeeHandler implements RequestStreamHandler {
    private String DYNAMO_TABLE = "Employees_M";
    private JSONParser parser = new JSONParser();
    private JSONObject responseObject = new JSONObject();
    private JSONObject responseBody = new JSONObject();
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private DynamoDB dynamoDB = new DynamoDB(client);
    private Gson gson = new Gson();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


        int employeeId;
        Item resItem = null;

        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);

            //pathPerameters
            if (reqObject.get("pathParameters") != null) {

                JSONObject pps = (JSONObject) reqObject.get("pathParameters");

                if (pps.get("employeeId") != null) {
                    employeeId = Integer.parseInt(pps.get("employeeId").toString());
                    resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("employeeId", employeeId);
                }
            }
            //queryStringParameters
            else if (reqObject.get("queryStringParameters") != null) {

                JSONObject qps = (JSONObject) reqObject.get("queryStringParameters");
                if (qps.get("employeeId") != null) {
                    employeeId = Integer.parseInt(qps.get("employeeId").toString());
                    resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("employeeId", employeeId);
                }
            }

            if (resItem != null) {

                Employee employee = new Employee(resItem.toJSON());
                responseBody.put("employee", employee);
                responseObject.put("statusCode", 200);
            } else {

                responseBody.put("message", "No Item Found");
                responseObject.put("statusCode", 404);
            }

            responseObject.put("body", responseBody.toString());
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

    public void handlePutRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            if (reqObject.get("body") != null) {
                Employee employee = new Employee((String) reqObject.get("body"));

                dynamoDB.getTable(DYNAMO_TABLE)
                        .putItem(new PutItemSpec().withItem((new Item())
                                .withNumber("employeeId", employee.getEmployeeId())
                                .withString("employeeName", employee.getEmployeeName())
                                .withString("workType", String.valueOf(employee.getWorkType()).toUpperCase())
                                .withNumber("wage", employee.getWage())
                                .withNumber("wageAfterTax",employee.getWageAfterTax())));

                responseBody.put("message", "New Item create/update");
                responseObject.put("statusCode", 200);
                responseObject.put("body", responseBody.toString());
            }
        } catch (Exception e) {
            responseObject.put("statusCode", 400);
            responseObject.put("error", e);
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

    public void getAllItems(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder employees = new StringBuilder();


        try {

            ScanRequest scanRequest = new ScanRequest()
                                            .withTableName(DYNAMO_TABLE);
            ScanResult result = client.scan(scanRequest);

            employees.append("[");
            for (Map<String, AttributeValue> item : result.getItems()){
                employees.append(gson.toJson(ItemUtils.toSimpleMapValue(item)) + ",");
            }
            employees.append("]");

            if (employees != null) {

                responseBody.put("Employees", employees);
                responseObject.put("statusCode", 200);
            } else {

                responseBody.put("message", "No Item Found");
                responseObject.put("statusCode", 404);
            }

            responseObject.put("body", responseBody.toString());
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

    // Take input from the user as an amount and return the filtered list of employees with higher than the input amount.
    public void getFilteredEmployees(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder employees = new StringBuilder();
        String wage;
        JSONObject parameters = null;


        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);

            //pathPerameters
            if (reqObject.get("pathParameters") != null) {

                parameters = (JSONObject) reqObject.get("pathParameters");
            }
            //queryStringParameters
            else if (reqObject.get("queryStringParameters") != null) {

                parameters = (JSONObject) reqObject.get("queryStringParameters");
            }

            if (parameters.get("wage") != null){

                wage = parameters.get("wage").toString();

                Map<String, AttributeValue> expressionAttributeValues =
                        new HashMap<String, AttributeValue>();
                expressionAttributeValues.put(":val", new AttributeValue().withN(wage));

                ScanRequest scanRequest = new ScanRequest()
                        .withTableName(DYNAMO_TABLE)
                        .withFilterExpression("wage > :val")
                        .withExpressionAttributeValues(expressionAttributeValues);
                ScanResult result = client.scan(scanRequest);

                employees.append("[");
                for (Map<String, AttributeValue> item : result.getItems()){
                    employees.append(gson.toJson(ItemUtils.toSimpleMapValue(item)) + ",");
                }
                employees.append("]");
            }

            if (employees != null) {

                responseBody.put("Employees", employees);
                responseObject.put("statusCode", 200);
            } else {

                responseBody.put("message", "No Item Found");
                responseObject.put("statusCode", 404);
            }

            responseObject.put("body", responseBody.toString());
        }
        catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
        }


        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }


    public void handleDeleteRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);
            if (reqObject.get("pathParameters")!=null){

                JSONObject pps = (JSONObject) reqObject.get("pathParameters");
                if(pps.get("employeeId")!=null){

                    int employeeId = Integer.parseInt(pps.get("employeeId").toString());
                    dynamoDB.getTable(DYNAMO_TABLE).deleteItem("employeeId",employeeId);
                }
            }

            responseBody.put("message","Item deleted");
            responseObject.put("statusCode", 200);
            responseObject.put("body", responseBody.toString());
        }
        catch (Exception e) {
            responseObject.put("statusCode", 400);
            responseObject.put("error", e);
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

}