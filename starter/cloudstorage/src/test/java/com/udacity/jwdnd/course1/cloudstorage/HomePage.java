package com.udacity.jwdnd.course1.cloudstorage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class HomePage {
    private final JavascriptExecutor jse;
    private final WebDriver driver;

    @FindBy(id = "logoutButton")
    private WebElement logoutButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        jse = (JavascriptExecutor) driver;

        PageFactory.initElements(driver, this);
    }

    public void logoutUser() {
        logoutButton.click();
    }

    public void clickTab(String elementTabId) {
        jse.executeScript("arguments[0].click()", driver.findElement(By.id(elementTabId)));
    }

    public void addNewNote() {
        jse.executeScript("arguments[0].click()", driver.findElement(By.id("addNoteButton")));

        jse.executeScript("document.getElementById('note-title').value='kage';");
        jse.executeScript("document.getElementById('note-description').value='shadow';");

        jse.executeScript("arguments[0].click()", driver.findElement(By.id("noteSubmit")));
    }

    public void editNote() {
        jse.executeScript("arguments[0].click()", driver.findElement(By.className("editNoteButton")));

        jse.executeScript("document.getElementById('note-title').value='kage-v2';");
        jse.executeScript("document.getElementById('note-description').value='shadow-v2';");

        jse.executeScript("arguments[0].click()", driver.findElement(By.id("noteSubmit")));
    }

    public void deleteNote() {
        WebElement userTable = driver.findElement(By.id("userTable"));

        jse.executeScript("arguments[0].click()", userTable.findElement(By.className("deleteNoteButton")));
    }

    public Integer getNotesListSize() {
        WebElement userTableAfterDelete = driver.findElement(By.id("userTable"));
        List<WebElement> notesList = userTableAfterDelete.findElements(By.className("noteTitleText"));

        return notesList.size();
    }

    public String getNoteTitle() {
        WebElement notesTable = driver.findElement(By.id("userTable"));

        List<WebElement> notesListTitle = notesTable.findElements(By.className("noteTitleText"));
        String noteTitle = notesListTitle.get(0).getAttribute("innerHTML");

        return noteTitle;
    }

    public void addNewCredential() {
        jse.executeScript("arguments[0].click()", driver.findElement(By.id("addCredentialButton")));

        jse.executeScript("document.getElementById('credential-url').value='www.naruto.com';");
        jse.executeScript("document.getElementById('credential-username').value='team7';");
        jse.executeScript("document.getElementById('credential-password').value='jinchuriki';");

        jse.executeScript("arguments[0].click()", driver.findElement(By.id("credentialSubmit")));
    }

    public void editCredential() {
        jse.executeScript("document.getElementById('credential-username').value='boruto';");
        jse.executeScript("document.getElementById('credential-password').value='himawara';");

        jse.executeScript("arguments[0].click()", driver.findElement(By.id("otherSubmit")));
    }

    public void deleteCredential() {
        WebElement credentialTable = driver.findElement(By.id("credentialTable"));
        jse.executeScript("arguments[0].click()", credentialTable.findElement(By.className("deleteCredentialButton")));
    }

    public int getCredentialsListSize() {
        WebElement credentialTableFinal = driver.findElement(By.id("credentialTable"));
        List<WebElement> credentialList = credentialTableFinal.findElements(By.className("editCredentialButton"));

        return credentialList.size();
    }

    public String getCredentialDecryptedPassword() {
        jse.executeScript("arguments[0].click()", driver.findElement(By.className("editCredentialButton")));

        String editFormDecryptedPass = jse
                .executeScript("return document.getElementById(\"credential-password\").value")
                .toString();

        return editFormDecryptedPass;
    }

    public String getCredentialEncryptedPassword() {
        WebElement tableWithNewCredentials = driver.findElement(By.id("credentialTable"));
        List<WebElement> newCredentialsList = tableWithNewCredentials.findElements(By.className("credentialPassword"));
        String decryptedPassword = newCredentialsList.get(0).getAttribute("innerHTML");

        return decryptedPassword;
    }

    public String getCredentialUsername() {
        WebElement tableWithNewCredentials = driver.findElement(By.id("credentialTable"));
        List<WebElement> newCredentialsList = tableWithNewCredentials.findElements(By.className("credentialUsername"));
        String username = newCredentialsList.get(0).getAttribute("innerHTML");

        return username;
    }

}
