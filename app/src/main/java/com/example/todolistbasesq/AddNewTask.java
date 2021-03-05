package com.example.todolistbasesq;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.todolistbasesq.Model.ToDoModel;
import com.example.todolistbasesq.SQUtils.DataBaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

/* 말그대로 newTask 동작처리 */
public class AddNewTask  extends BottomSheetDialogFragment {
    /* class에 extends BottomSheetDialogFragment 하여서 하단부쪽의 DialogStyle 생성.  */

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;

    private DataBaseHandler db; /* newTaskText => db = > recyclerView = > save 진행처리  */

    /*메모리상에 항상 하나만 존재할수 있게 하도록 싱글톤패턴 생성 이 객체를 만들때마다 계속 생성방지를 위해서 메모리절약*/
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL , R.style.DialogStyle);
        /*이렇게 setStyle을 하여 생명주기 생성 */

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task , container , false);
        Objects.requireNonNull(
                getDialog()).getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//리사이징 실행.
        return view;
    }


    /*onViewCreated 는 onCreateView에서 return해준 view를 가지고 있다.
     * 프래그먼트에서는 뷰를 생성하는 시점이 정해져 있어 onCreateView() 에서 생성
     * 하고 뷰가 만들어지면 onViewCreated()가 콜백. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*getView()로써의 findViewById 진행. */
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        /*Bundle객체로써 add한 task를 받을준비. 받고나서 db로 전송*/

        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task); //해당 task값 setText 해주고
            if(task.length()>0){
                newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
            }
        }

        //db insert
        db = new DataBaseHandler(getActivity());
        db.openDatabase();

        //EditText의 ChangedListener 인데 문자열값 감지해서 버튼사용 가능or불가능과 색 조정하는 구문
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){ /*  공백이라면 setEnabled(false); 즉 공백은 데이터를 넣을수 없다. */
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        // isUpdate 이 null or not ?
        //어댑터에서 값을 bundle로써 받았을때 finalIsUpdate가 true
        //아닐떄 false
        newTaskSaveButton.setOnClickListener(v -> {
            String text = newTaskText.getText().toString();
            if(finalIsUpdate){
                //값을 받았을떄 updateTask 진행.
                db.updateTask(bundle.getInt("id"), text);
            }
            else {
                //두 인자값을 db로 인서트
                ToDoModel model = new ToDoModel();
                model.setTask(text);
                model.setStatus(0);
                db.insertTask(model);
            }
            dismiss();
        });

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener)

            /* DialogCloseListener 를 포함하고 있다면 handleDialogClose를 진행/ 당연히 다이얼로그
            내려줘야하니까 */

            ((DialogCloseListener)activity).handleDialogClose(dialog);
        //dialog닫는 인터페이스를 onDismiss일때 실행
    }
}
