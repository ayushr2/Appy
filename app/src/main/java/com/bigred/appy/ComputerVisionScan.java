package com.bigred.appy;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.Scores;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ComputerVisionScan extends AppCompatActivity {

    public EmotionServiceClient emotionServiceClient = new EmotionServiceRestClient("d34faac9f21e411bb53245daa6e141cb");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvscan);

        final Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.happy);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(mBitmap);



        //convert bitmap to stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Button btnPRocess = (Button) findViewById(R.id.btnEmotion);
        btnPRocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 AsyncTask<InputStream, String, List<RecognizeResult>> emotionTask = new AsyncTask<InputStream, String, List<RecognizeResult>>()


                {
                    ProgressDialog mDialog = new ProgressDialog(ComputerVisionScan.this);
                    @Override
                    protected List<RecognizeResult> doInBackground(InputStream... params) {
                        try {
                            publishProgress("Recognizing....");
                            List<RecognizeResult> result = emotionServiceClient.recognizeImage(params[0]);
                            return result;
                        }
                        catch (Exception ex)
                        {
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                      }

                    @Override
                    protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                        mDialog.dismiss();
                        for(RecognizeResult res: recognizeResults)
                        {
                            String status = getEmo(res);
                            imageView.setImageBitmap(ImageHelper.drawRectonBitMap(mBitmap,res.faceRectangle,status));
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                      }
                };

                emotionTask.execute(inputStream);

            }
        });
    }



    private  String getEmo(RecognizeResult res) {
        List<Double> list = new ArrayList<>();
        Scores scores = res.scores;

        list.add(scores.anger);
        list.add(scores.happiness);
        list.add(scores.contempt);
        list.add(scores.disgust);
        list.add(scores.fear);
        list.add(scores.neutral);
        list.add(scores.sadness);
        list.add(scores.surprise);


        Collections.sort(list);

        double maxNum = list.get(list.size() - 1);
        if(maxNum == scores.anger)
            return "Anger";

        else if(maxNum == scores.happiness)
            return "Happy";

        else if(maxNum == scores.contempt)
            return "Contempt";

        else if(maxNum == scores.disgust)
            return "Disgust";

        else if(maxNum == scores.fear)
            return "Fear";

        else if(maxNum == scores.neutral)
            return "Neutral";

        else if(maxNum == scores.sadness)
            return "Sadness";

        else if(maxNum == scores.surprise)
            return "Surprise";
        else
            return "Neutral";


    }
}