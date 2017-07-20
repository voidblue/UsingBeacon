package com.doteam.usingbeacon;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


// 비콘이 쓰이는 클래스는 BeaconConsumer 인터페이스를 구현해야한다.
class MainActivity extends Activity implements BeaconConsumer,TextToSpeech.OnInitListener {
    int i=0;
    //id 56456비콘이 30미터 이내에 있을 시 한번만 음성출력하게 하는것
    boolean beacon56456 = true;

    TextToSpeech TTS;
    private BeaconManager beaconManager;
    // 감지된 비콘들을 임시로 담을 리스트

    private List<Beacon> beaconList = new ArrayList<>();
    private List<Beaconsupport> beaconsupportList = new ArrayList<>();
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beacon56456 = true;

        //TTS객체 , text to speech
        TTS = new TextToSpeech(this,this);
        // 실제로 비콘을 탐지하기 위한 비콘매니저 객체를 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        textView = (TextView)findViewById(R.id.TextView);

        // 여기가 중요한데, 기기에 따라서 setBeaconLayout 안의 내용을 바꿔줘야 하는듯 싶다.
        // 필자의 경우에는 아래처럼 하니 잘 동작했음.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);
    }

    //프로그램이 꺼질때 서비스를 종료
    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.shutdown();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }

    // 버튼이 클릭되면 textView 에 비콘들의 정보를 뿌린다.

    public void OnButtonClicked(View v){
        TTS.speak("비콘 탐지를 시작합니다.", TTS.QUEUE_FLUSH, null);
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
       //     textView.setText("");
            //i=0;
            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            for (Beacon beacon : beaconList) {
                textView.setText("");
                textView.append("ID : " + beacon.getId2() + " / " + "Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n");
                if( Double.parseDouble(String.format("%.3f", beacon.getDistance()))<30) {
                    //parse안에 있는 부분 하나하나 지정해줘야 한번씩만 실행할 수 있음.
                    if(Identifier.parse("56456").equals(beacon.getId2())&&beacon56456) {

                        TTS.speak("ID" + beacon.getId2() + "비콘이 30m거리에 있습니다.", TTS.QUEUE_FLUSH, null);
                        beacon56456 = false;
                   }
                }
            }


                // 자기 자신을 1초마다 호출
                handler.sendEmptyMessageDelayed(0, 1000);

            }

    };

    @Override
    public void onInit(int i) {

    }
}