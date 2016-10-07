package in.kosacki.dragndropwithnougat.comparators;

import java.io.File;
import java.util.Comparator;

/**
 * Created by hubert on 07/10/16.
 */
public class FileNameComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
        return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
    }
}
