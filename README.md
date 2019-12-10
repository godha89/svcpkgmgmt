# svcpkgmgmt

## This is the service which is used to create, update, delete and get packages and their products

To Run the application, follow these steps:


1.  Build the Project with following command in command prompt in the project directory
	gradlew bootRun
2.  Once build is completed, access the swagger page usingg following url
	<http://localhost:8761/swagger-ui.html#/package45controller>

3.	I have integrated an embedded H2 database which can be accessed at following url post application startup.
	<http://localhost:8761/h2>	
	


The REST API endpoints are documented in the Swagger page created for the application and can be accessed at:
<http://localhost:8761/swagger-ui.html>


#####  ID for each package is auto generated.

###### API List



*   Create Package -
   
	REST-API: http://localhost:8761/api/package/create   
	METHOD: POST   
	REQUEST: `{ "description": "Package Description", "name": "Package Name", "products": [{ "id" : "Product_ID_1"}]}`
    
	RESPONSE: HTTP_STATUS 201 CREATED


*   Get Package -

	REST-API: http://localhost:8761/api/package/fetchPackage/{id}?curr=<CURRENCY>   
	METHOD: GET   
	RESPONSE: `{"description": "string","id": "string","name": "string","price": 0,"products": [{"id": "string","name": "string","usdPrice": 0}]}`    
	

*   Delete Package - 
  
	REST-API: http://localhost:8761/api/package/deletePackage/{id}
	METHOD: DELETE   
	RESPONSE: HTTP_STATUS 200     


*   Update Package -

	REST-API: http://localhost:8761/api/package/updatePackage/{id}
	METHOD: PUT
	REQUEST: `{ "description": "Package Description", "name": "Package Name", "products": [{ "id" : "Product_ID_1", "name" : "Product_NAME_1", "usdPrice" : PRODUCT_AMT_DOUBLE}]}`
    
	RESPONSE: `{ "description": "Package Description", "name": "Package Name", "products": [{ "id" : "Product_ID_1", "name" : "Product_NAME_1", "usdPrice" : PRODUCT_AMT_DOUBLE}]}`
	RESPONSE: HTTP_STATUS 200     


*   Get All Packages -   

	REST-API: http://localhost:8761/api/package/allPackages   
	METHOD: GET   
	RESPONSE: `[{"description": "string","id": "string","name": "string","price": 0}]` 
	




Sample Request for Create Package:

{
  "description": "Package Description",
  
  "name": "Package Name",
  
  "products": [
   {
  "id" : "Product_ID_1",
  "name" : "Product_NAME_1",
  "usdPrice" : PRODUCT_AMT_DOUBLE
	}
  ]
}

