import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class Main {
    public static void main(String[] args) throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path dir = Path.of("/Users/f.wolff/Downloads");
        try {
            WatchKey key = dir.register(watcher,
                    ENTRY_CREATE,
                    ENTRY_DELETE,
                    ENTRY_MODIFY);
        } catch (IOException x) {
            System.err.println(x);
        }

        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                String msg = "";
                if (OVERFLOW.equals(kind)) {
                    System.out.println("OVERFLOW!");
                    continue;
                } else if (ENTRY_CREATE.equals(kind)) {
                    msg = "Was created: %s";
                } else if (ENTRY_DELETE.equals(kind)) {
                    msg = "Was deleted: %s";
                } else if (ENTRY_MODIFY.equals(kind)) {
                    msg = "Was modified: %s";
                }

                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                System.out.println(String.format(msg, filename.toString()));
            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }
}
