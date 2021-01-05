package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.models.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.models.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    private final CredentialService credentialService;
    private final NoteService noteService;

    public HomeController(CredentialService credentialService, NoteService noteService) {
        this.credentialService = credentialService;
        this.noteService = noteService;
    }

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        String currentUsername = authentication.getName();
        // Used for adding attributes for thymeleaf to iterate
        model.addAttribute("notes", noteService.getUserNotes(currentUsername));
        model.addAttribute("credentials", credentialService.getUserCredentials(currentUsername));

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

        // ensures that the user is redirected back to homepage
        return "redirect:/home";
    }

    @PostMapping("delete/note/{noteId}")
    public String deleteNote(Authentication authentication, @PathVariable("noteId") Integer noteId) {
        String currentUsername = authentication.getName();

        noteService.deleteNote(noteId, currentUsername);

        // ensures that the user is redirected back to homepage
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

        // ensures that the user is redirected back to homepage
        return "redirect:/home";
    }

    @PostMapping("delete/credential/{credentialId}")
    public String deleteCredential(Authentication authentication, @PathVariable("credentialId") Integer credentialId) {
        String currentUsername = authentication.getName();

        credentialService.deleteCredential(credentialId, currentUsername);

        // ensures that the user is redirected back to homepage
        return "redirect:/home";
    }

    @ModelAttribute("credentialFields")
    public CredentialForm credentialForm() {
        return new CredentialForm();
    }
    // Credential ends here
}
