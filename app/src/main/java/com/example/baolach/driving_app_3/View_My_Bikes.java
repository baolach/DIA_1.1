package com.example.baolach.driving_app_3;

/**
 * Created by Baolach on 14/03/2017.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class View_My_Bikes extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String username,des_text;
    Integer id2;
    Button addbike ;
    static ArrayList<Integer> bikesId = new ArrayList<Integer>();

    ListView listView;
    ArrayList<Bike> list;
    BikeListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_bikes);
        bikesId.clear();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            username = bundle.getString("Name");
        }

        addbike =  (Button) findViewById(R.id.addbike);


        listView = (ListView) findViewById(R.id.listview);
        list = new ArrayList<>();
        adapter = new BikeListAdapter(this,R.layout.bike_items,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        new Thread(new Runnable(){

            public void run()
            {
                try
                {

                    PreparedStatement st = null;
                    Class.forName("com.mysql.jdbc.Driver");
                    String url = "jdbc:mysql://178.62.50.210:3306/bikes";
                    Connection c = DriverManager.getConnection(url,"maciek","maciek93");
                    String sql = "select bike_id, bike_name, make, image, model, frame_no, bike_type from user_bikes where username=? and stolen=?";
                    st = c.prepareStatement(sql);
                    st.setString(1, username);
                    st.setString(2, "no");
                    ResultSet rs = st.executeQuery();


                    while(rs.next())
                    {
                        int id = rs.getInt("bike_id");
                        bikesId.add(rs.getInt("bike_id"));
                        String name = rs.getString("bike_name");
                        String make = rs.getString("make");
                        Blob blob = rs.getBlob("image");
                        String model = rs.getString("model");
                        String frame = rs.getString("frame_no");
                        String des = rs.getString("bike_type");
                        int blobLength = (int) blob.length();
                        byte[] image = blob.getBytes(1, blobLength);
                        blob.free();
                        list.add(new Bike(id, name, make, model, frame, des, image));
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    st.close();
                    c.close();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }).start();


        addbike.setOnClickListener(new View.OnClickListener(){

            public void onClick (View v)
            {
//                Intent k = new Intent(View_My_Bikes.this, Add_New_Bike.class);
//                k.putExtra("Name", username.toString());
//                startActivity(k);
//                finish();
            }
        });

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setTitle("Theft Description");
        final EditText input2 = new EditText(this);
        input2.setInputType(InputType.TYPE_CLASS_TEXT );
        builder2.setView(input2);


        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                des_text = input2.getText().toString();
                Intent k = new Intent(View_My_Bikes.this, View_My_Bikes.class);
                k.putExtra("Name", username.toString());
                new Thread(new Runnable(){

                    public void run()
                    {
                        mark_stolen();
                    }

                }).start();
                startActivity(k);
                finish();
            }
            protected void mark_stolen() {

                try
                {
                    PreparedStatement st = null;
                    Class.forName("com.mysql.jdbc.Driver");
                    String url = "jdbc:mysql://178.62.50.210:3306/bikes";
                    Connection c = DriverManager.getConnection(url,"maciek","maciek93");
                    String sql = "update user_bikes set stolen=?, theft_place=? where username=? and bike_id=?";
                    st = c.prepareStatement(sql);
                    st.setString(1,"yes");
                    st.setString(2,des_text);
                    st.setString(3, username);
                    st.setInt(4, id2);
                    st.execute();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Bike marked as stolen !", Toast.LENGTH_LONG).show();
                        }
                    });

                    st.close();
                    c.close();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }


        });
        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });






        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What do you want to do? ");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent k = new Intent(View_My_Bikes.this, View_My_Bikes.class);
                k.putExtra("Name", username.toString());
                new Thread(new Runnable(){

                    public void run()
                    {
                        delete_bike();
                    }

                }).start();
                startActivity(k);
                finish();
            }
            protected void delete_bike() {

                try
                {
                    PreparedStatement st = null;
                    Class.forName("com.mysql.jdbc.Driver");
                    String url = "jdbc:mysql://178.62.50.210:3306/bikes";
                    Connection c = DriverManager.getConnection(url,"maciek","maciek93");
                    String sql = "delete from user_bikes where username=? and bike_id=?";
                    st = c.prepareStatement(sql);
                    st.setString(1, username);
                    st.setInt(2, id2);
                    st.execute();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Bike deleted !", Toast.LENGTH_LONG).show();
                        }
                    });

                    st.close();
                    c.close();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Mark as stolen!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //builder2.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemId = (int) id;
                id2 =  bikesId.get(itemId);
                Toast.makeText(getBaseContext(), "username + id"  + username + "  " + id2, Toast.LENGTH_LONG).show();
                //builder.show();
                return true;
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent k = new Intent(View_My_Bikes.this, Bike_Details.class);
        int itemId = (int) id;
        //Toast.makeText(getBaseContext(), "username + id" + id2 + username, Toast.LENGTH_LONG).show();
        System.out.println(id2 + username);
        k.putExtra("Name", username.toString());
        k.putExtra("Bike_id",(bikesId.get(itemId)));
        startActivity(k);
        finish();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent k = new Intent(View_My_Bikes.this, Menu.class);
            k.putExtra("Name", username.toString());
            startActivity(k);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}