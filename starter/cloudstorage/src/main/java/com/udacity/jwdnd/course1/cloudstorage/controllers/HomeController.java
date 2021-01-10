package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.models.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.models.File;
import com.udacity.jwdnd.course1.cloudstorage.models.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class HomeController {
    private final CredentialService credentialService;
    private final NoteService noteService;
    private final FileService fileService;
    private final String errorMessage = "Something went wrong! Try again.";

    public HomeController(CredentialService credentialService, NoteService noteService, FileService fileService) {
        this.credentialService = credentialService;
        this.noteService = noteService;
        this.fileService = fileService;
    }

    /*
    * Most of the requests redirects back to home after completing an action
    * instead of staying to the route where the request was made.
    * */

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        String currentUsername = authentication.getName();

        // Used for adding attributes for thymeleaf to iterate
        model.addAttribute("notes", noteService.getUserNotes(currentUsername));
        model.addAttribute("credentials", credentialService.getUserCredentials(currentUsername));
        model.addAttribute("files", fileService.getUserFiles(currentUsername));

        return "home";
    }

    // Notes starts here
    @PostMapping("/note")
    public String addNote(
            Authentication authentication,
            @ModelAttribute("noteFields") NoteForm noteFields,
            RedirectAttributes redirectAttributes
    ) {
        String currentUsername = authentication.getName();

        boolean isANewNote = noteFields.getNoteId().length() == 0;
        String successMessage = "";

        if (isANewNote) {
            int rowsAdded = noteService.addNote(noteFields, currentUsername);

            if (rowsAdded > 0) {
                successMessage = "New note added successfully! See notes tab";
            }
        } else {
            int rowsEdited = noteService.editNote(noteFields, currentUsername);

            if (rowsEdited > 0) {
                successMessage = "Note edited successfully! See notes tab";
            }
        }

        if (successMessage.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        }

        return "redirect:/home";
    }

    @PostMapping("delete/note/{noteId}")
    public String deleteNote(
            Authentication authentication,
            @PathVariable("noteId") Integer noteId,
            RedirectAttributes redirectAttributes
    ) {
        String currentUsername = authentication.getName();
        int rowsDeleted = noteService.deleteNote(noteId, currentUsername);

        if (rowsDeleted > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "Note deleted successfully! See notes tab");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/home";
    }


    @ModelAttribute("noteFields")
    public NoteForm noteForm() {
        return new NoteForm();
    }

    // Notes ends here

    // Credential starts here
    @PostMapping("/credential")
    public String addCredential(
            Authentication authentication,
            @ModelAttribute("credentialFields") CredentialForm credentialFields,
            RedirectAttributes redirectAttributes
    ) {
        String currentUsername = authentication.getName();
        boolean isANewCredential = credentialFields.getCredentialId().length() == 0;
        String successMessage = "";

        if (isANewCredential) {
            int rowsAdded = credentialService.addCredential(credentialFields, currentUsername);

            if (rowsAdded > 0) {
                successMessage = "New credential added successfully! See credentials tab";
            }
        } else {
            int rowsEdited = credentialService.editCredential(credentialFields, currentUsername);

            if (rowsEdited > 0) {
                successMessage = "Credential edited successfully! See credentials tab";
            }
        }

        if (successMessage.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        }

        return "redirect:/home";
    }

    @PostMapping("delete/credential/{credentialId}")
    public String deleteCredential(
            Authentication authentication,
            @PathVariable("credentialId") Integer credentialId,
            RedirectAttributes redirectAttributes
    ) {
        String currentUsername = authentication.getName();
        int rowsDeleted = credentialService.deleteCredential(credentialId, currentUsername);

        if (rowsDeleted > 0) {
            redirectAttributes.addFlashAttribute("successMessage", "Credential deleted successfully! See credentials tab");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }

        return "redirect:/home";
    }

    @ModelAttribute("credentialFields")
    public CredentialForm credentialForm() {
        return new CredentialForm();
    }
    // Credential ends here

    // File starts here
    @PostMapping("/file/upload")
    public String addFile(
            Authentication authentication,
            @RequestParam("fileUpload") MultipartFile fileUpload,
            RedirectAttributes redirectAttributes
    ) {
        String currentUsername = authentication.getName();

        if (fileUpload.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot upload empty file.");
            return "redirect:/home";
        }

        if (fileService.isFileNameUnavailable(currentUsername, fileUpload.getOriginalFilename())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot upload file with the same name.");
            return "redirect:/home";
        }

        try {
            this.fileService.addFile(fileUpload, currentUsername);
            redirectAttributes.addFlashAttribute("successMessage", "File successfully uploaded!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            e.printStackTrace();
        }

        return "redirect:/home";
    }

    @GetMapping("/file/download")
    public ResponseEntity downloadFileFromLocal(
            Authentication authentication,
            @RequestParam(value = "filename") String filename
    ) {
        String currentUsername = authentication.getName();
        File file = fileService.getUserFile(currentUsername, filename);

        byte[] fileData = file.getFileData();

        InputStream inputStream = new ByteArrayInputStream(fileData);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, filename)
                .body(resource);
    }

    @PostMapping("delete/file/{fileId}")
    public String deleteFile(
            Authentication authentication,
            @PathVariable("fileId") Integer fileId,
            RedirectAttributes redirectAttributes
    ) {
        String currentUsername = authentication.getName();

        fileService.deleteFile(fileId, currentUsername);
        redirectAttributes.addFlashAttribute("successMessage", "File deleted successfully!");

        return "redirect:/home";
    }

    // File ends here
}
