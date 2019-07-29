package jp.naist.ahclab.speechkit;
import java.io.DataOutputStream;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.ProgressBar;

public class PcmRecorder implements Runnable {
	private volatile boolean isRecording;
	private volatile boolean isExiting;
	private final Object mutex = new Object();
	public static final int frequency = 16000;
//	public static final int frequency = 44100;
	private static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private static final String TAG = "PcmRecorder";
	int bufferRead = 0;
	byte[] tempBuffer;
	int cAmplitude;
	int bufferSize ;
	RecorderListener rl;
	AudioRecord recordInstance ;
    DataOutputStream output; //*********

	public PcmRecorder() {
		android.os.Process.setThreadPriority(
            android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		bufferSize = Math.max(
		    AudioRecord.getMinBufferSize(frequency,
		        AudioFormat.CHANNEL_IN_MONO, audioEncoding),
		    (frequency * 2) / 4);  // 16-bit samples, 4x per second
		
		tempBuffer = new byte[bufferSize];
		recordInstance = new AudioRecord(
				MediaRecorder.AudioSource.MIC, frequency,
				AudioFormat.CHANNEL_IN_MONO, audioEncoding, bufferSize);
        Log.i("PcmRecorder", "current state of AudioRecord: " + recordInstance.getState());
        //ProgressBar pb;
        //pb = (ProgressBar)findViewById(R.id.progressbar);
	}

	public void run() {
		synchronized (mutex) {
			while (!this.isRecording && !this.isExiting) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
					throw new IllegalStateException("Wait() interrupted!", e);
				}
			}
		}

        Log.i("PcmRecorder", "AudioRecord State before record: " + recordInstance.getState());
        boolean recording = false;
		while(!this.isExiting) {
            try{
                recordInstance.startRecording();
                break;
            }
            catch(IllegalStateException e){
                e.printStackTrace();
                try{Thread.sleep(1000);}
                catch(InterruptedException f){}
               // throw new IllegalStateException("Sleep() intterupted!", e);
            }
 //           catch(IllegalStateException e){Thread.sleep(1000);throw new InterruptedException("sleep() interrupted!");}
//            catch(InterruptedException f){throw new InterruptedException("sleep() interrupted!");}
        }
        Log.i("PcmRecorder", "AudioRecord State after record: " + recordInstance.getState());

		while (this.isRecording && !this.isExiting) {
			bufferRead = recordInstance.read(tempBuffer, 0, bufferSize);
			
			if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException(
						"read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}
			rl.onRecorderBuffer(tempBuffer);
			
    //        double sum = 0;
    //      	for (int i = 0; i < bufferRead; i++) {
    //            output.writeShort(tempBuffer [i]);
    //            sum += tempBuffer [i] * tempBuffer [i];
    //        }
    //        if (bufferRead > 0) {
    //            final double amplitude = sum / bufferRead;
    //            pb.setProgress((int) Math.sqrt(amplitude));
    //        }

			/*
			for (int i=0; i<bufferRead/2; i++)
			{ // 16bit sample size
				short curSample = getShort(tempBuffer[i*2], tempBuffer[i*2+1]);
				if (curSample > cAmplitude)
				{ // Check amplitude
					cAmplitude = curSample;
				}
			}
			*/
			//this.ws_client_speech.send(tempBuffer);
			
		}
		recordInstance.stop();
	}

	public int getMaxAmplitude(){
		int result = cAmplitude;
		cAmplitude = 0;
		return result;
	}
	
	public void setRecording(boolean isRecording) {
		synchronized (mutex) {
			this.isRecording = isRecording;
			if (this.isRecording) {
				mutex.notify();
			}
		}
	}

    public void shutdownThreads() {
        this.isExiting = true;
        setRecording(false);
    }

	public boolean isRecording() {
		synchronized (mutex) {
			return isRecording;
		}
	}

	public void setOutputFile(String audioPath) {
		// TODO Auto-generated method stub
		
	}
	public int getSampleRate() {
		// TODO Auto-generated method stub
		return frequency;
	}
	public void setListener(Recognizer recognizer) {
		// TODO Auto-generated method stub
		rl = recognizer;
	}
}
