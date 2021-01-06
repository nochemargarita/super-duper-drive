package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mappers.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.mappers.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.models.File;
import com.udacity.jwdnd.course1.cloudstorage.models.Note;
import com.udacity.jwdnd.course1.cloudstorage.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {
    private UserMapper userMapper;
    private FileMapper fileMapper;

    public FileService(UserMapper userMapper, FileMapper fileMapper) {
        this.userMapper = userMapper;
        this.fileMapper = fileMapper;
    }

    public File[] getUserFiles(String username) {
        User user = userMapper.getUser(username);

        return fileMapper.getFiles(user.getUserId());
    }

    public File getUserFile(String username, String fileName) {
        User user = userMapper.getUser(username);

        return fileMapper.getFile(user.getUserId(), fileName);
    }

    public int addFile(MultipartFile file, String username) throws IOException {
        User user = userMapper.getUser(username);

        return this.fileMapper.insert(
                new File(
                        null,
                        file.getOriginalFilename(),
                        file.getContentType(),
                        String.valueOf(file.getSize()),
                        user.getUserId(),
                        file.getBytes()));
    }

    public int deleteFile(int fileId, String username) {
        User user = userMapper.getUser(username);

        return fileMapper.delete(fileId, user.getUserId());
    }

    public boolean isFileNameUnavailable(String username, String fileName) {
        User user = userMapper.getUser(username);

        return fileMapper.getFile(user.getUserId(), fileName) != null;
    }
}
