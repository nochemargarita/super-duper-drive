package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.models.Note;
import com.udacity.jwdnd.course1.cloudstorage.models.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.models.User;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;

    public NoteService(NoteMapper noteMapper, UserMapper userMapper) {
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
    }

    public Note[] getUserNotes(String username) {
        User user = userMapper.getUser(username);

        return noteMapper.getNotes(user.getUserId());
    }

    public int addNote(NoteForm noteFields, String username) {
        User user = userMapper.getUser(username);

        return noteMapper.insert(new Note(null, noteFields.getNoteTitle(), noteFields.getNoteDescription(), user.getUserId()));
    }

    public int editNote(NoteForm noteFields, String username) {
        User user = userMapper.getUser(username);
        Note note = noteMapper.getNote(user.getUserId(), Integer.parseInt(noteFields.getNoteId()));

        note.setNoteTitle(noteFields.getNoteTitle());
        note.setNoteDescription(noteFields.getNoteDescription());

        return noteMapper.edit(note);
    }

    public int deleteNote(int noteId, String username) {
        User user = userMapper.getUser(username);

        return noteMapper.delete(noteId, user.getUserId());
    }

}
