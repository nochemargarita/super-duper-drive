package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage {
    @FindBy(id = "inputFirstName")
    private WebElement firstName;

    @FindBy(id = "inputLastName")
    private WebElement lastName;

    @FindBy(id = "inputUsername")
    private WebElement username;

    @FindBy(id = "inputPassword")
    private WebElement password;

    @FindBy(id = "signupSubmitButton")
    private WebElement submitButton;

    @FindBy(id = "signupSuccessMessage")
    private WebElement successMessage;

    @FindBy(id = "signupErrorMessage")
    private WebElement errorMessage;

    public SignupPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void setFormValues(String firstNameInput, String lastNameInput, String usernameInput, String passwordInput) {
        firstName.sendKeys(firstNameInput);
        lastName.sendKeys(lastNameInput);
        username.sendKeys(usernameInput);
        password.sendKeys(passwordInput);

        submitButton.click();
    }

    public String getSuccessMessage() {
        return successMessage.getText();
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

}
