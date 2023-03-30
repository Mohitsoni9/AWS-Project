package org.emp.AWS;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExecuteStatementRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.emp.AWS.payrollSystem.Employee;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.*;
import java.util.Map;

public class EmployeeHandler implements RequestStreamHandler {
    private String DYNAMO_TABLE = "Employees_M";

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // this will help us parse the request object
        JSONParser parser = new JSONParser(); // we will add to this object for our api response
        JSONObject responseObject = new JSONObject();// we will add the item to this object
        JSONObject responseBody = new JSONObject();// we will add the item to this object

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        int employeeId;
        Item resItem = null;

        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);

            //pathPerameters
            if (reqObject.get("pathParameters") != null) {

                JSONObject pps = (JSONObject) reqObject.get("pathParameters");

                if (pps.get("employeeId") != null) {
                    employeeId = Integer.parseInt((String) pps.get("employeeId"));
                    resItem = dynamoDB.getTable(DYNAMO_TABLE).getItem("employeeId", employeeId);
                }
            }
            //queryStringParameters
            else if (reqObject.get("queryStringParameters") != null) {

                JSONObject qps = (JSONObject) reqObject.get("queryStringParameters");
                if (qps.get("employeeId") != null) {
                    employeeId = Integer.parseInt((String) qps.get("employeeId"));
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // this will help us parse the request object
        JSONParser parser = new JSONParser(); // we will add to this object for our api response
        JSONObject responseObject = new JSONObject();// we will add the item to this object
        JSONObject responseBody = new JSONObject();// we will add the item to this object

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            if (reqObject.get("body") != null) {
                Employee employee = new Employee((String) reqObject.get("body"));

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
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        StringBuilder tableContent = new StringBuilder();

        JsonElement employees = null;

        try {

            ScanRequest scanRequest = new ScanRequest()
                    .withTableName(DYNAMO_TABLE);
            ScanResult result = client.scan(scanRequest);

            for (Map<String, AttributeValue> item : result.getItems()){
                tableContent.append(ItemUtils.toSimpleMapValue(item));
            }

            employees = JsonParser.parseString(tableContent.toString());
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


    public void handleDeleteRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // this will help us parse the request object
        JSONParser parser = new JSONParser(); // we will add to this object for our api response
        JSONObject responseObject = new JSONObject();// we will add the item to this object
        JSONObject responseBody = new JSONObject();// we will add the item to this object

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {

            JSONObject reqObject = (JSONObject) parser.parse(reader);
            if (reqObject.get("pathParameters")!=null){

                JSONObject pps = (JSONObject) reqObject.get("pathParameters");
                if(pps.get("employeeId")!=null){

                    int employeeId = Integer.parseInt((String)pps.get("employeeId"));
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