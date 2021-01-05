package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.models.Note;
import com.udacity.jwdnd.course1.cloudstorage.models.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.models.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.FileService;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    private CredentialService credentialService;
    private FileService fileService;
    private NoteService noteService;
    private UserService userService;

    public HomeController(CredentialService credentialService, FileService fileService, NoteService noteService, UserService userService) {
        this.credentialService = credentialService;
        this.fileService = fileService;
        this.noteService = noteService;
    }

    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        String currentUsername = authentication.getName();
        // Used for adding attributes for thymeleaf to iterate
        model.addAttribute("notes", noteService.getUserNotes(currentUsername));

        return "home";
    }

    // Notes starts here
    @PostMapping("/note")
    public String addNote(Authentication authentication, @ModelAttribute("noteFields") NoteForm noteFields, Model model) {
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

        return "redirect:/home";
    }


    @ModelAttribute("noteFields")
    public NoteForm noteForm() {
        return new NoteForm();
    }

    // Notes ends here




}
