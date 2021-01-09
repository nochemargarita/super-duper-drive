package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {
	private final String firstName = "Naruto";
	private final String lastName = "Uzumaki";
	private final String username = "team7";
	private final String password = "jinchuriki";

	@LocalServerPort
	private int port;

	private WebDriver driver;

	private SignupPage signup;
	private LoginPage login;
	private HomePage home;
	private String baseURL;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void testRouteAccess() {
		baseURL = "http://localhost:" + port;

		driver.get(baseURL + "/login");
		login = new LoginPage(driver);

		assertEquals("Login", driver.getTitle());

		driver.get(baseURL + "/signup");
		signup = new SignupPage(driver);

		assertEquals("Sign Up", driver.getTitle());

		driver.get(baseURL + "/home");
		home = new HomePage(driver);

		assertEquals("Login", driver.getTitle());

		driver.get(baseURL + "/home");
		home = new HomePage(driver);

		assertEquals("Login", driver.getTitle());
	}

	@Test
	public void testSignupLoginLogout() {
		baseURL = "http://localhost:" + port;
		driver.get(baseURL + "/signup");
		signup = new SignupPage(driver);

		signup.setFormValues(firstName, lastName, username + "2", password);
		assertEquals("You successfully signed up! Please continue to the login page.", signup.getSuccessMessage());

		driver.get(baseURL + "/login");
		login = new LoginPage(driver);

		assertEquals("Login", driver.getTitle());

		login.setFormValues(username, password);

		driver.get(baseURL + "/home");
		home = new HomePage(driver);

		assertEquals("Home", driver.getTitle());
	}

	@Test
	public void testNoteCRUD() {
		String noteTitle = "kage";

		baseURL = "http://localhost:" + port;
		driver.get(baseURL + "/signup");
		signup = new SignupPage(driver);
		signup.setFormValues(firstName, lastName, username, password);

		driver.get(baseURL + "/login");
		login = new LoginPage(driver);
		login.setFormValues(username, password);

		driver.get(baseURL + "/home");
		home = new HomePage(driver);

		// Write a test that creates a note, and verifies it is displayed.
		home.clickTab("nav-notes");
		home.addNewNote();
		String newNoteTitle = home.getNoteTitle();
		int afterAddNotesListSize = home.getNotesListSize();

		assertEquals(1, afterAddNotesListSize);
		assertEquals(noteTitle, newNoteTitle);

		// Write a test that edits an existing note and verifies that the changes are displayed.
		home.editNote();
		home.clickTab("nav-notes");
		String editedNoteTitle = home.getNoteTitle();
		int afterEditNotesListSize = home.getNotesListSize();

		assertEquals(1, afterEditNotesListSize);

		assertEquals(noteTitle + "-v2", editedNoteTitle);

		// Write a test that deletes a note and verifies that the note is no longer displayed.
		home.clickTab("nav-notes");
		home.deleteNote();
		int afterDeleteNotesListSize = home.getNotesListSize();

		assertEquals(0, afterDeleteNotesListSize);
	}

	@Test
	public void testCredentialCRUD() {
		baseURL = "http://localhost:" + port;
		driver.get(baseURL + "/signup");
		signup = new SignupPage(driver);
		signup.setFormValues(firstName, lastName, username, password);

		driver.get(baseURL + "/login");
		login = new LoginPage(driver);
		login.setFormValues(username, password);

		driver.get(baseURL + "/home");
		home = new HomePage(driver);

		/*
		Write a test that creates a set of credentials, verifies that they are displayed, and
		verifies that the displayed password is encrypted.
		*/
		home.clickTab("nav-credentials");
		home.addNewCredential();

		int afterAddCredentialListSize = home.getCredentialsListSize();
		String newCredentialUsername = home.getCredentialUsername();
		String newCredentialEncryptedPassword = home.getCredentialEncryptedPassword();

		assertEquals(1, afterAddCredentialListSize);
		assertEquals(username, newCredentialUsername);
		assertNotEquals(password, newCredentialEncryptedPassword);

		/*
		Write a test that views an existing set of credentials, verifies that the viewable password is unencrypted,
		edits the credentials, and verifies that the changes are displayed.
		*/
		home.clickTab("nav-credentials");
		String editFormDecryptedPass = home.getCredentialDecryptedPassword();

		assertEquals(password, editFormDecryptedPass);

		home.editCredential();
		home.clickTab("nav-credentials");

		int afterEditCredentialListSize = home.getCredentialsListSize();
		String editedCredentialUsername = home.getCredentialUsername();
		String editedCredentialEncryptedPassword = home.getCredentialEncryptedPassword();

		assertEquals(1, afterEditCredentialListSize);
		assertEquals("boruto", editedCredentialUsername);
		assertNotEquals("himawara", editedCredentialEncryptedPassword);

		/*
		Write a test that deletes an existing set of credentials and
		verifies that the credentials are no longer displayed.
		*/
		home.clickTab("nav-credentials");
		home.deleteCredential();

		int afterDeleteCredentialListSize = home.getCredentialsListSize();

		assertEquals(0, afterDeleteCredentialListSize);
	}
}
