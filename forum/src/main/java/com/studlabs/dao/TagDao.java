package com.studlabs.dao;

import com.studlabs.bll.exceptions.DaoException;
import com.studlabs.bll.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    List<Tag> findAll() throws DaoException;

    Tag save(Tag object) throws DaoException;

    Optional<Tag> getTag(String tagName) throws DaoException;

    void remove(int id) throws DaoException;

    void saveTagToThread(int tagId, int threadId) throws DaoException;

    List<Tag> getTagsByThreadId(int threadId) throws DaoException;

    void deleteTagFromThread(int tagId, int threadId) throws DaoException;

    List<Tag> find(List<String> tags) throws DaoException;

    void saveTagList(List<String> tags) throws DaoException;

    void deleteTagList(List<String> tags) throws DaoException;

}
