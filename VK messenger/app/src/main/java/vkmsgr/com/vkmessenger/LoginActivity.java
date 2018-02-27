package vkmsgr.com.vkmessenger;


import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendsRecycleViewAdapter friendsRecViewAdapter;
    private EditText etFindFriend;
    private ImageButton btnFilter;
    private DataBaseRequest dataBaseRequest;
    boolean[] checkFilterItems = {false, false, false};

    private static final String[] scope = new String[]{
            VKScope.FRIENDS,
            VKScope.MESSAGES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dataBaseRequest = new DataBaseRequest(getApplicationContext());

        etFindFriend = (EditText) findViewById(R.id.et_find_friend);
        etFindFriend.addTextChangedListener(textWatcher);

        btnFilter = (ImageButton) findViewById(R.id.btn_Filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFilter();
            }
        });

        if (!VKSdk.isLoggedIn())
            VKSdk.login(this, scope);
        else
            createRecycleView(dataBaseRequest.queryFriends());
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            friendsRecViewAdapter.updateList(dataBaseRequest.find(editable.toString(), checkFilterItems));
            friendsRecViewAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loginactivity, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // boolean oneIsChecked = false;
        dataBaseRequest.deleteAll();
        VKSdk.logout();
        VKSdk.login(this, scope);
        return super.onOptionsItemSelected(item);
    }

    private void createRecycleView(List<Friend> list){
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_friends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        friendsRecViewAdapter = new FriendsRecycleViewAdapter(list);
        recyclerView.setAdapter(friendsRecViewAdapter);
        friendsRecViewAdapter.setListener(new FriendsRecycleViewAdapter.Listener() {
            @Override
            public void onClick(int id, String name) {
                dialogNewMessage(id, name);
            }
        });
    }

    private void dialogNewMessage(final int id, String nameFriend){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View dialogView =  layoutInflater.inflate(R.layout.send_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialogView);

        final EditText etNewMsg = (EditText) dialogView.findViewById(R.id.dialog_input_text);
        final TextView tvNewMsg = (TextView) dialogView.findViewById(R.id.dialog_name_friend);

        tvNewMsg.setText(nameFriend);
        builder.setCancelable(true).setPositiveButton(getResources().getText(R.string.send), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(etNewMsg.getText())){
                    VKRequest request = new VKRequest("messages.send",
                                                                VKParameters.from(VKApiConst.USER_ID, id,
                                                                VKApiConst.MESSAGE, etNewMsg.getText()));
                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.send_message_ok), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.send_message_error), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
            // Пользователь успешно авторизовался
                VKRequest request = VKApi.friends().get(
                            VKParameters.from(VKApiConst.FIELDS, "photo_100, first_name, last_name, sex, online"));
                request.useSystemLanguage = true;
                request.executeWithListener(new VKRequest.VKRequestListener() {

                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKUsersArray usersArray = (VKUsersArray) response.parsedModel;
                        dataBaseRequest.insertList(usersArray);
                        createRecycleView(dataBaseRequest.queryFriends());
                    }
                });
            }

            @Override
            public void onError(VKError error) {
            // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_autorization), Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void dialogFilter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        final boolean[] oldCheckFilterItems = new boolean[3];
        for (int i =0; i<3; i++)
            oldCheckFilterItems[i] = checkFilterItems[i];
        final String[] checkItem = {getResources().getString(R.string.online), getResources().getString(R.string.male), getResources().getString(R.string.female)};

        builder.setTitle(getResources().getText(R.string.param_find))
                .setCancelable(false)
                .setMultiChoiceItems(checkItem, checkFilterItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                checkFilterItems[i] = b;
                            }
                });
        builder.setCancelable(false)
                .setPositiveButton(getResources().getText(R.string.ok),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                friendsRecViewAdapter.updateList(dataBaseRequest.find(etFindFriend.getText().toString(), checkFilterItems));
                friendsRecViewAdapter.notifyDataSetChanged();
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false)
                .setNegativeButton(getResources().getText(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j =0; j<3; j++)
                            checkFilterItems[j] = oldCheckFilterItems[j];
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
