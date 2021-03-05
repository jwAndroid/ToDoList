package com.example.todolistbasesq.SQUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.todolistbasesq.Model.ToDoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*1.SQ 오픈헬퍼를 중심으로 구현됨 이쪽에서 대부분 처리하며 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHandler" ;
    /*2. 테이블 , 명 , 변수 생성  */
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo"; //테이블 명
    private static final String ID = "id"; //user ID
    private static final String TASK = "task"; // TEXT (할일 데이터)
    private static final String STATUS = "status"; // 체크상태
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";
    //ID , TASK , STATUS 3가지 넣고
    //띄어쓰기 주의할것

    private SQLiteDatabase sqLiteDatabase;

    public DataBaseHandler(Context context){
        super(context , NAME , null , VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
        /*  execSQL 로써 테이블 INSERT */
        /* 데이터베이스를 처음 생성해주는 경우 execSQL => 생성 단 , 파라메터로 db를 던져서 진행 */

    }

    //onUpgrade는 기존에 테이블이 이미 생성되있고 이것을 업그레이드(수정)하는것을 구현
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //오래된 테이블을 DROP.
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        sqLiteDatabase = this.getWritableDatabase();
        //데이터베이스에 쓸수 있는 권한리턴
    }


    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        //ContentValues는 ContentResolver가 사용하는 한마디로 사용할데이터(id,task,status)에 대한 운송 수단역할을 갖게됨.
        /* task 파라미터를 받아서 ContentValues로써 put */
        cv.put(TASK, task.getTask()); //TASK에 다가 넣고
        cv.put(STATUS, 0); // 스테츄에 넣고
        //put메서드를 사용해서 (항목, 값)을 DB테이블 순서에 맞게 집어넣습니다.
        sqLiteDatabase.insert(TODO_TABLE, null, cv);
        //최종적으로 sqLiteDatabase로써 TODO_TABLE 에 cv를 insert 진행
    }



    /* ******************* */
    public List<ToDoModel> getAllTasks(){
        /*데이터를 꺼낼 List객체생성 */
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cursor = null;
        /**/
        /*Cursor 인터페이스 진행 기본적으로 DB에서 값을 가져와서 마치 실제 Table의 행을
        참조하는 것 처럼 사용 할 수 있게 해준다. */
        sqLiteDatabase.beginTransaction();

        try{
            //TODO_TABLE만 뺴고 전부 null값 처리
            /* sqLiteDatabase 쿼리 어디를 ?? 위쪽에서 insert 한 TODO_TABLE 을 요청
            cursor로써 다음 레코드로 진행하기위해 반복문처리*/

            cursor = sqLiteDatabase.query(TODO_TABLE, null, null,
                    null, null, null, null, null);

            if (cursor != null){
                if (cursor.moveToFirst()){
                    //커서가 첫번째 행이라면..
                    do{
                        ToDoModel task = new ToDoModel();
                        //모델 가져와서 해당하는 그 인덱스의값을 get 해준후에 , 모델에 set해주고 최종적으로 list에 add해주는
                        //작업을 진행한다.
                        task.setId(cursor.getInt(cursor.getColumnIndex(ID))); //해당 ID값은 무엇인지?
                        task.setTask(cursor.getString(cursor.getColumnIndex(TASK))); // 내용물 무엇을 적었는지?
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS))); // true or false 체크 상태인지 아닌지?
                        taskList.add(task);
                        /*다음 레코드로 넘어가면서 여기서 set해주고 이 객체 자체에 list를 반환하게 하면 됨 */
                        //cursor.getColumnIndex(x) x값을 반환하여 getInt(x) 이 값을 model에 set

                    }while (cursor.moveToNext());/*반복해서 다음 레코드 커서로 진행 */

//                    for(int i = 0; i < cursor.getCount(); i++){
//                              cursor.moveToNext()
//                    }

                }
            }

        }catch (Exception e){
            Log.d(TAG , e.getMessage());

        }finally {
            //finally 구문으로 꼭 END 와 close 하는 작업을 실행
            sqLiteDatabase.endTransaction();
            assert cursor != null;
            cursor.close();
        }

        return taskList; //리턴값은 최종적으로 89라인에서 add해준 List를 반환함.
        // 리사이클러뷰 어댑터 장착해주고 DB  => LIST = > RECYCLERVIEW 이친구를 뿌려주면 끝.
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        sqLiteDatabase.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
        //수정기능 마찬가지로 ContentValues를 사용 :  status값 update
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        sqLiteDatabase.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
        //수정기능 마찬가지로 ContentValues를 사용 : task(내용)값 update
    }

    public void deleteTask(int id){
        sqLiteDatabase.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
        //delete는 따로 ContentValues 사용하지않고 delete 실행
    }


}
