package com.example.mytodo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTaskDialog.AddNewTaskCallback, TaskAdapter.TaskItemEventListener,
        EditTaskDialog.EditTaskCallback {

    private TaskAdapter adapter;
    private RecyclerView recyclerView;
    private TodoDatabase db;
    private List<Task> tasks;
    private EditText searchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SqlScoutServer.create(this, getPackageName());
        adapter = new TaskAdapter(this);

        EditText searchEt = findViewById(R.id.et_main_search);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    List<Task> tasks = db.searchTask(s.toString());
                    adapter.setTasks(tasks);
                } else {
                    List<Task> tasks = db.getTasks();
                    adapter.setTasks(tasks);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.delete_all_title)
                        .setMessage(R.string.delete_all_mesage)

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.clearAllTask();
                                adapter.clearItems();
                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

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
        db.updateTak(task);

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