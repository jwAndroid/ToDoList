package com.example.todolistbasesq.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistbasesq.AddNewTask;
import com.example.todolistbasesq.MainActivity;
import com.example.todolistbasesq.Model.ToDoModel;
import com.example.todolistbasesq.R;
import com.example.todolistbasesq.SQUtils.DataBaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private static final String TAG = "ToDoAdapter" ;
    private List<ToDoModel> todoList;
    private DataBaseHandler db;
    private MainActivity activity;


    public ToDoAdapter(DataBaseHandler db , MainActivity activity){
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_layout, viewGroup, false);
        return new ViewHolder(itemView);

        /*아이템 ui 인플레이터 */

    }

    /*1일때 체크고 0일때 해제임 */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*바인드뷰홀더에서도 db open 진행*/
        db.openDatabase();

        /*db 객체에서 insert 해준값을 set 해줌. */
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        /* 체크박스여서 두부분(text 와 체크) 가능 */

        //체크박스 체인지 리스너와 데이터베이스 처리 그리고 체크상태 처리
        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                db.updateStatus(item.getId(), 1);
                holder.task.setTextColor(Color.parseColor("#3949AB"));
                /*번들사용해서 모델데이터 전송후 --> 드로우어 레이아웃 설정에서 파싱*/
//                Log.d(TAG , "체크");
            } else {
                db.updateStatus(item.getId(), 0);
                holder.task.setTextColor(Color.parseColor("#000000"));
//                Log.d(TAG , "취소");
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }


    private boolean toBoolean(int n) {
        return n != 0;
    }

    public Context getContext() {
        return activity;
        /* 액티비티로써 받아 처리 */
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
        /*리스트만 담아서 노티파이 진행 */
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());


        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle); /*번들객체 전송*/
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);/*파라메터로 받은 리스트의 포지션 할당*/
        db.deleteTask(item.getId()); /* 아이디 넣고서 딜리트 진행 */
        todoList.remove(position);
        notifyItemRemoved(position); /*데이터변경시 노티파이 진행 */
    }

    /* 이부분 아이템은 task_layout이니까 체크박스 view 하나 받아서 진행  */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;

        public ViewHolder(@NonNull View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);

        }
    }


}
