package maximedelange.btcminerstatistics.Screens.Screens;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import maximedelange.btcminerstatistics.R;
import maximedelange.btcminerstatistics.Screens.Adapter.PoolAdapter;
import maximedelange.btcminerstatistics.Screens.Domain.BlockNumber;
import maximedelange.btcminerstatistics.Screens.Domain.JSONParser;
import maximedelange.btcminerstatistics.Screens.Domain.Pool;

public class PoolScreen extends AppCompatActivity {
    // Fields
    private HashMap<Integer, HashMap<Integer, BlockNumber>> pools = null;
    private BlockNumber blockNumber = null;
    private HashMap<Integer, BlockNumber> blockNumbers = null;
    private JSONParser jsonParser = null;
    private ListView listView = null;
    private ArrayList<BlockNumber> blockNumberList = null;
    private PoolAdapter poolAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        changeNavigationBar();
        pools = new HashMap<>();
        jsonParser = new JSONParser();
        apiCall();
        showListOfBlocks();
        showDetailedPoolInformation();
    }

    public void changeNavigationBar(){
        setTitle("Pool information");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#071e22")));
    }

    public void showListOfBlocks(){
        blockNumberList = new ArrayList<>();
        for(Map.Entry<Integer, HashMap<Integer, BlockNumber>> pool : pools.entrySet()){
            blockNumbers = pool.getValue();
        }

        for(Map.Entry<Integer, BlockNumber> blockNumber : blockNumbers.entrySet()){
            blockNumberList.add(blockNumber.getValue());
        }

        BlockNumber[] blocks = new BlockNumber[blockNumberList.size()];
        blocks = blockNumberList.toArray(blocks);

        poolAdapter = new PoolAdapter(this, blocks);
        listView = (ListView) findViewById(R.id.listviewPool);
        //listView.setAdapter(new PoolAdapter(this, blocks));
        listView.setAdapter(poolAdapter);
    }

    public void showDetailedPoolInformation(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent detailedPoolInformation = new Intent(view.getContext(), PoolDetailScreen.class);
                Object blockObject =  poolAdapter.getItem(position);
                blockNumber = (BlockNumber)blockObject;

                System.out.println(blockNumber);
                System.out.println(blockNumber.getConfirmation());

                detailedPoolInformation.putExtra("poolInformation", (Serializable) blockNumber);
                startActivity(detailedPoolInformation);
            }
        });
    }

    public void apiCall(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pools = jsonParser.parsePoolInformation();
    }

}
