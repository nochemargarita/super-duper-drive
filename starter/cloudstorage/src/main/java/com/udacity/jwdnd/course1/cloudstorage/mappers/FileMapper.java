package com.udacity.jwdnd.course1.cloudstorage.mappers;

import com.udacity.jwdnd.course1.cloudstorage.models.File;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FileMapper {
    @Select("SELECT * FROM FILES WHERE userid = #{userId}")
    File[] getFiles(int userId);

    @Select("SELECT * FROM FILES WHERE userid = #{userId} AND filename = #{filename}")
    File getFile(int userId, String filename);

    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) VALUES(#{filename}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(File file);

    @Delete("DELETE FROM FILES WHERE fileid = #{fileId} AND userid = #{userId}")
    int delete(int fileId, int userId);
}
