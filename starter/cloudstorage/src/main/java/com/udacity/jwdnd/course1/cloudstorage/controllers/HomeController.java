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
    public String addNote(Authentication authentication, @ModelAttribute("noteFields") NoteForm noteFields) {
        String currentUsername = authentication.getName();

        boolean isANewNote = noteFields.getNoteId().length() == 0;

        if (isANewNote) {
            noteService.addNote(noteFields, currentUsername);
        } else {
            noteService.editNote(noteFields, currentUsername);
        }

        return "redirect:/home";
    }

    @PostMapping("delete/note/{noteId}")
    public String deleteNote(Authentication authentication, @PathVariable("noteId") Integer noteId) {
        String currentUsername = authentication.getName();

        noteService.deleteNote(noteId, currentUsername);

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
            @ModelAttribute("credentialFields") CredentialForm credentialFields
    ) {
        String currentUsername = authentication.getName();
        boolean isANewCredential = credentialFields.getCredentialId().length() == 0;

        if (isANewCredential) {
            credentialService.addCredential(credentialFields, currentUsername);
        } else {
            credentialService.editCredential(credentialFields, currentUsername);
        }

        return "redirect:/home";
    }

    @PostMapping("delete/credential/{credentialId}")
    public String deleteCredential(Authentication authentication, @PathVariable("credentialId") Integer credentialId) {
        String currentUsername = authentication.getName();

        credentialService.deleteCredential(credentialId, currentUsername);

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
            redirectAttributes.addFlashAttribute("message", "Cannot upload empty file.");
            return "redirect:/home";
        }

        if (fileService.isFileNameUnavailable(currentUsername, fileUpload.getOriginalFilename())) {
            redirectAttributes.addFlashAttribute("message", "Cannot upload file with the same name.");
            return "redirect:/home";
        }

        try {
            this.fileService.addFile(fileUpload, currentUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/home";
    }

    @GetMapping("/file/download")
    public ResponseEntity downloadFileFromLocal(Authentication authentication, @RequestParam(value = "filename") String filename) {
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
    public String deleteFile(Authentication authentication, @PathVariable("fileId") Integer fileId) {
        String currentUsername = authentication.getName();

        fileService.deleteFile(fileId, currentUsername);

        return "redirect:/home";
    }

    // File ends here
}
