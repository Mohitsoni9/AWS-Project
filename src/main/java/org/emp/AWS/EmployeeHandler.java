package org.emp.AWS;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.emp.AWS.payrollSystem.*;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.io.*;
import java.util.Map;

public class EmployeeHandler implements RequestStreamHandler {
    private String DYNAMO_TABLE = "Employees_M";

    /**
     * Get Employee by employeeId.
     */
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        int employeeId;
        Item resItem = null;

        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);

            //pathPerameters
            if (reqObject.get("pathParameters") != null) {

                JSONObject pps = (JSONObject) reqObject.get("pathParameters");

                /**
                 * EmployeeId it must be in integer format.
                 */
                if (pps.get("employeeId") != null) {

                    employeeId = Integer.parseInt(
                                        Validation.checkEmployeeId(
                                                pps.get("employeeId").toString()));
                    resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("employeeId", employeeId);
                }
            }

            //queryStringParameters
            else if (reqObject.get("queryStringParameters") != null) {

                JSONObject qps = (JSONObject) reqObject.get("queryStringParameters");

                /**
                 * EmployeeId it must be in integer format.
                 */
                if (qps.get("employeeId") != null) {
                    employeeId = Integer.parseInt(
                            Validation.checkEmployeeId(
                                    qps.get("employeeId").toString()));
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
        }
        catch (Exception e) {

            context.getLogger().log("Error: " + e.getMessage());
            responseBody.put("Error", e.getMessage());
            responseObject.put("statusCode", 500);
            responseObject.put("body", responseBody.toString());
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

    //Register the Employee
    public void handlePutRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            if (reqObject.get("body") != null) {

                Employee employee = new Employee((String) reqObject.get("body"));
                Validation.checkEmployee(employee);

                dynamoDB.getTable(DYNAMO_TABLE)
                        .putItem(new PutItemSpec().withItem((new Item())
                                .withNumber("employeeId", employee.getEmployeeId())
                                .withString("employeeName", employee.getEmployeeName())
                                .withString("workType", String.valueOf(employee.getWorkType()))
                                .withNumber("wage", employee.getWage())
                                .withNumber("wageAfterTax",employee.getWageAfterTax())));

                responseBody.put("message", "New Item create/update");
                responseObject.put("statusCode", 200);
                responseObject.put("body", responseBody.toString());
            }
        } catch (Exception e) {

            context.getLogger().log("Error: " + e.getMessage());
            responseBody.put("Error", e.getMessage());
            responseObject.put("statusCode", 500);
            responseObject.put("body", responseBody.toString());
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

    //get all employees from the table
    public void getAllItems(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        Gson gson = new Gson();

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

    /**
     * Take input from the user as an amount and return the filtered list of employees with higher than the input amount.
      */
    public void getFilteredEmployees(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        Gson gson = new Gson();
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

    // For deleting the Employee from the table.
    public void handleDeleteRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);
        int employeeId;
        Item resItem = null;

        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);
            if (reqObject.get("pathParameters")!=null){

                JSONObject pps = (JSONObject) reqObject.get("pathParameters");
                if(pps.get("employeeId")!=null){

                    employeeId = Integer.parseInt(
                            Validation.checkEmployeeId(
                                    pps.get("employeeId").toString()));

                    /**
                     * if employee is not found in the database
                     */
                    resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("employeeId", employeeId);
                    if (resItem == null){
                        throw new Exception("Employee is not registered");
                    }


                    dynamoDB.getTable(DYNAMO_TABLE).deleteItem("employeeId",employeeId);
                }
            }

            responseBody.put("message","Item deleted");
            responseObject.put("statusCode", 200);
            responseObject.put("body", responseBody.toString());
        }
        catch (Exception e) {

            context.getLogger().log("Error: " + e.getMessage());
            responseBody.put("Error", e.getMessage());
            responseObject.put("statusCode", 500);
            responseObject.put("body", responseBody.toString());
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

}