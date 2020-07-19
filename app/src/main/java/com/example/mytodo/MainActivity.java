package com.example.mytodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTaskDialog.AddNewTaskCallback, TaskAdapter.TaskItemEventListener,
        EditTaskDialog.EditTaskCallback {
    TaskAdapter adapter;
    RecyclerView recyclerView;
    TodoDatabase db;
    List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SqlScoutServer.create(this, getPackageName());
        adapter = new TaskAdapter(this);


        recyclerView = findViewById(R.id.rv_main_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        db = new TodoDatabase(this);
        tasks = db.getTasks();
        adapter.addItems(tasks);

        View clearTskBtn = findViewById(R.id.iv_main_clearTask);
        clearTskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.clearAllTask();
                adapter.clearItems();
            }
        });

        View addNewTaskFab = findViewById(R.id.fab_main_addNewTask);
        addNewTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTaskDialog dialog= new AddTaskDialog();
                dialog.show(getSupportFragmentManager(),null);

            }
        });

    }

    @Override
    public void onNewTask(Task task) {
        long taskId = db.addTask(task);
        if (taskId != -1) {
            task.setId(taskId);
            adapter.addItem(task);
        } else {
            Log.e("MainActivity", "onNewTask: item did not inserted");
        }
    }

    @Override
    public void onDeleteButtonClick(Task task) {
        int result = db.deleteTask(task);
        if (result>0){
            adapter.deleteItem(task);
            Toast.makeText(this, "ایتم حذف شد", Toast.LENGTH_SHORT).show();
        }else {

        }
    }

    @Override
    public void onItemLongPress(Task task) {
        EditTaskDialog editTaskDialog = new EditTaskDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("TASK",task);
        editTaskDialog.setArguments(bundle);
        editTaskDialog.show(getSupportFragmentManager(),null);
    }

    @Override
    public void onItemCheckedChange(Task task) {

    }

    @Override
    public void onEditTask(Task task) {
      int result =  db.updateTak(task);
      if (result>0){
        adapter.updateItem(task);

      }else {

      }
    }
}