# CamelTestBD

Route that every 30 seconds read the table "REGIONS" in the schema HR of Oracle and print each Region.

## To perform the unit test you need:

1) Have a Oracle BD XE and to assign the password "welcome1" to schema HR or modify the "blueprint.xml" with its own password
> NOTE: The connecction for my Oracle BD XE are: URL=jdbc:oracle:thin:@localhost:1521/xe, USER="hr", PASS="welcome1"

2) Create a BD Mock. It's in "src/test/sql". It have the same structure of the Oracle table. Moreover, three records are inserted for the simulation of the data in "REGIONS" Oracle table.

##### NOTE: The following steps are in the test class "SQLTest"
3) The method "setUp" is responsible for create the database, the table and insert three registers into the table.

4) The method "createRouteBuilder" is responsible for replacing the sql component with the new mock database Derby changing the "sql" componente for the "db" component.

5) In the method "testReadRegion", the function "getRouteDefinition" allow change the configuration in the EndPoints of the route. In the function "configure()" is changed the route for after invoqueing the EndPoint "_logRegion", will invoque the mock EndPoint "mock:testValidate"

6) The mock EndPoint is created. Also is created a List<String> of Regions will be come. Finally the mock Endpoint "mock:testValidate" expected the bodies of the List<String>.
> NOTE: The List<String> is fill up with the data of the mock database Derby.