package com.example.todo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ToDoItemDao {
    @Query("SELECT * FROM todolist")
    List<ToDoItem> listAll();

    @Query("SELECT * FROM todolist ORDER BY toDoItemChecked ASC, toDoItemDate ASC")
    List<ToDoItem> listAllSorted();

    @Insert
    void insert(ToDoItem toDoItem);

    @Insert
    void insertAll(ToDoItem... toDoItems);

    @Query("DELETE FROM todolist")
    void deleteAll();
}
