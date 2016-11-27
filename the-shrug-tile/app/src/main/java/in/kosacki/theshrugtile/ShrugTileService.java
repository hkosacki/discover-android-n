package in.kosacki.theshrugtile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Handler;
import android.os.Looper;
import android.service.quicksettings.TileService;
import android.widget.Toast;

/**
 * Created by hubert on 27.11.16.
 */

public class ShrugTileService extends TileService {

    public static final String SHRUG = "shrug";
    Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void onClick() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(SHRUG, getString(R.string.shrug));
        clipboard.setPrimaryClip(clip);
        mainThread.post(showToastMessage);
    }

    Runnable showToastMessage = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_shrug_copied), Toast.LENGTH_SHORT).show();
        }
    };
}
