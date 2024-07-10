import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class Task {

    private ExtentReports extent;
    private ExtentTest test;

    @BeforeClass
    public void setup() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extent.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        RestAssured.baseURI = "https://reqres.in/api";

    }

    @Test
    public void testGetUsers() {
        test = extent.createTest("GET Users", "Test to get list of users");

        Response response = RestAssured.get("/users");

        test.info("Sending GET request to https://reqres.in/api/users");
        test.info("Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 200);
        test.pass("Status code is 200");

        String contentType = response.header("Content-Type");
        Assert.assertTrue(contentType.contains("application/json"));
        test.pass("Content-Type is application/json");

        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("page"));
        Assert.assertTrue(responseBody.contains("per_page"));
        Assert.assertTrue(responseBody.contains("total"));
        Assert.assertTrue(responseBody.contains("total_pages"));
        Assert.assertTrue(responseBody.contains("data"));
    }

    @Test
    public void testGetSingleUser() {

        int userId = 2;

        test = extent.createTest("GET Single User", "Test to get details of a single user");

        Response response = RestAssured.get("/users/" + userId);

        test.info("Sending GET request to https://reqres.in/api/users/" + userId);
        test.info("Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 200);
        test.pass("Status code is 200");

        String contentType = response.header("Content-Type");
        Assert.assertTrue(contentType.contains("application/json"));
        test.pass("Content-Type is application/json");

        int id = response.jsonPath().getInt("data.id");
        String email = response.jsonPath().getString("data.email");
        String firstName = response.jsonPath().getString("data.first_name");
        String lastName = response.jsonPath().getString("data.last_name");

        Assert.assertEquals(id, userId);
        test.pass("User ID is " + userId);

        Assert.assertNotNull(email);
        test.pass("Email is present");

        Assert.assertNotNull(firstName);
        test.pass("First name is present");

        Assert.assertNotNull(lastName);
        test.pass("Last name is present");
    }

    @Test
    public void testCreateUser() {
        test = extent.createTest("POST Create User", "Test to create a new user");

        String requestBody = "{ \"name\": \"John\", \"job\": \"leader\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/users");

        test.info("Sending POST request to https://reqres.in/api/users with body: " + requestBody);
        test.info("Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 201);
        test.pass("Status code is 201");

    }

    @Test
    public void testUpdateUser() {
        test = extent.createTest("PUT Update User", "Test to update an existing user");

        String requestBody = "{ \"name\": \"John\", \"job\": \"developer\" }";

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .put("/users/2");

        test.info("Sending PUT request to https://reqres.in/api/users/2 with body: " + requestBody);
        test.info("Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 200);
        test.pass("Status code is 200");

    }

    @Test
    public void testDeleteUser() {
        test = extent.createTest("DELETE User", "Test to delete a user");

        Response response = RestAssured.delete("https://reqres.in/api/users/2");

        test.info("Sending DELETE request to https://reqres.in/api/users/2");
        test.info("Response: " + response.asString());

        Assert.assertEquals(response.getStatusCode(), 204);
        test.pass("Status code is 204");
    }

    @Test(dataProvider = "userData")
    public void testCreateUserParameterized(String email, String firstName, String lastName) {
        test = extent.createTest("POST Create User Parameterized", "Test to create a new user with different data");

        Response response_ = RestAssured.get("https://reqres.in/api/users/2");

        Response response = RestAssured.given()
                .when()
                .body("{\"email\":\"" + email + "\",\"first_name\":\"" + firstName + "\",\"last_name\":\"" + lastName + "\"}")
                .post("/users")
                .then()
                .assertThat()
                .statusCode(201)
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 201);
        test.pass("Status code is 201");
        
        String responseBody = response_.getBody().asString();
        Assert.assertTrue(responseBody.contains("email"));
        Assert.assertTrue(responseBody.contains("first_name"));
        Assert.assertTrue(responseBody.contains("last_name"));
    }

    @AfterClass
    public void teardown() {
        extent.flush();
    }

    @DataProvider(name = "userData")
    public Object[][] userData() {
        return new Object[][]{
                {"george.bluth@reqres.in", "George", "Bluth"},
                {"janet.weaver@reqres.in", "Janet", "Weaver"},
                {"emma.wong@reqres.in", "Emma", "Wong"}
        };
    }
}


