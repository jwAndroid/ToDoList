package com.example.todolistbasesq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.todolistbasesq.Adapter.ToDoAdapter;
import com.example.todolistbasesq.Model.ToDoModel;
import com.example.todolistbasesq.SQUtils.DataBaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private DataBaseHandler db;/*sqlite db 전역변수 생성 */

    private ToDoAdapter tasksAdapter; /*리사이클러뷰를 위한 어댑터*/
    private List<ToDoModel> taskList;/* 리사이클러뷰를 위한 ToDoModel모델에 대한 리스트 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide(); //액션바 지워주고

        db = new DataBaseHandler(this); //db객체 메모리에 올려주고
        db.openDatabase();//데이터베이스에 쓸수 있는 권한리턴
        taskList = new ArrayList<>(); //리스트 객체생성

        /* 리사이클러뷰 셋팅 */
        RecyclerView recyclerView = findViewById(R.id.tasksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter( db , MainActivity.this);
        /*첫번째 파라미터보면 LIST를 넣는게 아니고 db객체를 넣어서 진행하는것 . 이부분 유의 db.getList로써 진행*/
        recyclerView.setAdapter(tasksAdapter);

        /*아이템 터치 헬퍼 : 스와이프기능을 위해 추가 */
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);

        taskList = db.getAllTasks();
        //현재의 List에 db를 이용한 데이터를 getAllTasks()를 하여서 할당받는다.
        Collections.reverse(taskList); //거꾸로 뒤집기
        tasksAdapter.setTasks(taskList); // 리사이클러뷰를 위해 파라메터로 현재의 리스트를 던져준다

        fab.setOnClickListener(v ->
                    AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG
                            //newInstance()해서 사용용
               ));

    }

    /* **중요** 다이얼로그가 사라질때에도 데이터를 보존 or update 하기 위해서 notifyDataSetChanged(); 진행 */
    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        /* 이부분 노티파이 진행 */
        tasksAdapter.notifyDataSetChanged();
    }
}