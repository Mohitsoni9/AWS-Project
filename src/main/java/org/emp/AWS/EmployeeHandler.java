package org.emp.AWS;

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

            //queryStringParameters
            if (reqObject.get("queryStringParameters") != null) {

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

}