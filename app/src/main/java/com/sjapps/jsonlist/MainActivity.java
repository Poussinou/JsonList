package com.sjapps.jsonlist;

import static com.sjapps.jsonlist.java.JsonFunctions.*;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sjapps.about.AboutActivity;
import com.sjapps.adapters.ListAdapter;
import com.sjapps.jsonlist.java.ExceptionCallback;
import com.sjapps.jsonlist.java.JsonData;
import com.sjapps.jsonlist.java.ListItem;
import com.sjapps.library.customdialog.BasicDialog;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    ImageButton backBtn, menuBtn;
    Button openFileBtn;
    TextView titleTxt, emptyListTxt;
    ListView list;
    JsonData data = new JsonData();
    ProgressBar progressBar;
    TextView loadingFileTxt;
    boolean isMenuOpen;
    ListAdapter adapter;
    View menu, dim_bg;
    ViewGroup viewGroup;
    AutoTransition autoTransition = new AutoTransition();
    Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: resume");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        autoTransition.setDuration(150);
        menuBtn.setOnClickListener(view -> open_closeMenu());

        backBtn.setOnClickListener(view -> onBackPressed());
        openFileBtn.setOnClickListener(view -> ImportFromFile());

        menu.findViewById(R.id.openFileBtn2).setOnClickListener(view -> {
            ImportFromFile();
            open_closeMenu();
        });
        menu.findViewById(R.id.aboutBtn).setOnClickListener(view -> {
            OpenAbout();
            open_closeMenu();
        });
        dim_bg.setOnClickListener(view -> open_closeMenu());


        Intent intent  = getIntent();
        Log.d(TAG, "onCreate: " + intent);
        if (Intent.ACTION_VIEW.equals(intent.getAction())){

            Log.d(TAG, "onCreate: " + intent.getData());

            String FileData = FileSystem.LoadDataFromFile(this,intent.getData());
//            Log.d(TAG, "onCreate: " + FileData);
            if (FileData != null)
                LoadData(FileData);
            else
                Log.d(TAG, "onCreate: null data");
        }
        if (intent.getAction().equals("android.intent.action.OPEN_FILE")){
            ImportFromFile();
        }
    }

    private void OpenAbout() {
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }

    @Override
    public void onBackPressed() {

        if (isMenuOpen) {
            open_closeMenu();
            return;
        }

        if (adapter!= null && adapter.selectedItem != -1){
            adapter.selectedItem = -1;
            adapter.notifyDataSetChanged();
            return;
        }

        if (data.isEmptyPath()){
            BasicDialog dialog = new BasicDialog();
            dialog.Builder(this, true)
                    .setTitle("Exit?")
                    .setRightButtonText("Yes")
                    .onButtonClick(this::finish)
                    .show();
            return;
        }


        TransitionManager.beginDelayedTransition(viewGroup, autoTransition);


        String[] pathStrings = data.splitPath();
        data.clearPath();

        String Title = "";
        for (int i = 0; i < pathStrings.length-1; i++) {
            data.setPath(data.getPath().concat((data.isEmptyPath()?"":"///") + pathStrings[i]));
        }
        if (pathStrings.length > 1) {
            Title = JsonData.getName(pathStrings[pathStrings.length-2]);
        }

        open(Title, data.getPath());
        if (data.isEmptyPath()) {
            backBtn.setVisibility(View.GONE);
        }
    }

    private void initialize() {
        backBtn = findViewById(R.id.backBtn);
        menuBtn = findViewById(R.id.menuBtn);
        titleTxt = findViewById(R.id.titleTxt);
        emptyListTxt = findViewById(R.id.emptyListTxt);
        list = findViewById(R.id.list);
        openFileBtn = findViewById(R.id.openFileBtn);
        viewGroup = findViewById(R.id.content);
        menu = findViewById(R.id.menu);
        dim_bg = findViewById(R.id.dim_layout);
        progressBar = findViewById(R.id.progressBar);
        loadingFileTxt = findViewById(R.id.LoadFileTxt);
        dim_bg.bringToFront();
        menu.bringToFront();
        menuBtn.bringToFront();
    }

    private void open_closeMenu() {
        if (!isMenuOpen) {
            dim_bg.setVisibility(View.VISIBLE);
            menu.setVisibility(View.VISIBLE);
            menuBtn.setImageResource(R.drawable.ic_close);
            isMenuOpen = true;
        } else {
            dim_bg.setVisibility(View.INVISIBLE);
            menu.setVisibility(View.GONE);
            menuBtn.setImageResource(R.drawable.ic_menu);
            isMenuOpen = false;
        }


    }


    private void LoadData(String Data) {

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            ArrayList<ListItem> temp = data.getRootList();
            JsonElement element;
            try {
                element = JsonParser.parseString(Data);

            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(MainActivity.this, "File is too large", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
                return;
            }
            if (element == null) {
                handler.post(() -> {
                    Toast.makeText(MainActivity.this, "Filed to load file!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
                return;
            }

            if (element instanceof JsonObject) {
                Log.d(TAG, "run: Json object");
                JsonObject object = FileSystem.loadDataToJsonObj(element);
                Log.d(TAG, "LoadData: " + object);
                data.setRootList(getJsonObject(object,onReadJsonException));
            }
            if (element instanceof JsonArray) {
                Log.d(TAG, "run: Json array");
                JsonArray array = FileSystem.loadDataToJsonArray(element);
                Log.d(TAG, "LoadData: " + array);
                data.setRootList(getJsonArrayRoot(array,onReadJsonException));
            }

            if (!data.isRootListNull()) {
                handler.post(() -> {
                    TransitionManager.beginDelayedTransition(viewGroup, autoTransition);
                    adapter = new ListAdapter(data.getRootList(), MainActivity.this, "");
                    list.setAdapter(adapter);
                    openFileBtn.setVisibility(View.GONE);
                    backBtn.setVisibility(View.GONE);
                    titleTxt.setText("");
                    data.clearPath();
                });

            } else data.setRootList(temp);

            handler.post(() -> progressBar.setVisibility(View.GONE));

        }).start();


    }

    public void open(String Title, String path) {
        TransitionManager.beginDelayedTransition(viewGroup, autoTransition);

        if (isMenuOpen)
            open_closeMenu();

        if (emptyListTxt.getVisibility() == View.VISIBLE)
            emptyListTxt.setVisibility(View.GONE);

        data.setPath(path);
        titleTxt.setText(Title);
        ArrayList<ListItem> arrayList = getListFromPath(path,data.getRootList());
        adapter = new ListAdapter(arrayList, this, path);
        list.setAdapter(adapter);
        if (arrayList.size() == 0) {
            emptyListTxt.setVisibility(View.VISIBLE);
        }
        System.out.println("path = " + path);
        if (!path.equals("")) {
            backBtn.setVisibility(View.VISIBLE);
        }

    }

    private void ImportFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        ActivityResultData.launch(intent);
    }


    ActivityResultLauncher<Intent> ActivityResultData = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        if(result.getResultCode() == Activity.RESULT_CANCELED){
                            Toast.makeText(MainActivity.this,"Import data canceled",Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    if (result.getData() == null || result.getData().getData() == null){
                        Toast.makeText(MainActivity.this, "Fail to load data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //File
                    Uri uri = result.getData().getData();
                    progressBar.setVisibility(View.VISIBLE);
                    loadingFileTxt.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        String Data = FileSystem.LoadDataFromFile(MainActivity.this, uri);

                        if (Data == null) {
                            Log.d(TAG, "onActivityResult: null data");
                            return;
                        }
                        handler.post(() -> {
                            progressBar.setVisibility(View.GONE);
                            loadingFileTxt.setVisibility(View.GONE);
                            LoadData(Data);
                        });

                    }).start();

                }
            });

    ExceptionCallback onReadJsonException = e -> {
        Log.e(TAG, "getJsonObject: Failed to load data");
        handler.post(() -> Toast.makeText(MainActivity.this, "Failed to load data!", Toast.LENGTH_SHORT).show());
    };
}