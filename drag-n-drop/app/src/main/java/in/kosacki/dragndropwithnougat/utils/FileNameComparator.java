package in.kosacki.dragndropwithnougat.utils;

import java.io.File;
import java.util.Comparator;

/**
 * Created by kryst on 06.10.2016.
 */
public class FileNameComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
        return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(), file2.getName());
    }

}
