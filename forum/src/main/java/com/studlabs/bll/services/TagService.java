package com.studlabs.bll.services;

import com.studlabs.bll.exceptions.BllException;
import com.studlabs.bll.model.Tag;

import java.util.List;

public interface TagService {

    List<Tag> getAll() throws BllException;

    void saveTagList(List<String> tags) throws BllException;

    void deleteTagList(List<String> tags) throws BllException;
}
