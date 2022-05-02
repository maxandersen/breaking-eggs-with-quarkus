///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//JAVA_OPTIONS --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED
//DEPS org.asciidoctor:asciidoctorj:2.5.3
//DEPS org.asciidoctor:asciidoctorj-diagram:2.2.1
//DEPS org.asciidoctor:asciidoctorj-revealjs:5.0.0.rc1
//DEPS info.picocli:picocli:4.6.3

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static picocli.CommandLine.*;

@Command(name="adoc2reveal", version="1.0")
public class adoc2reveal implements Callable<Integer> {

    @Parameters(index="0", description = ".adoc file to convert/render.", defaultValue = "index.adoc")
    private File file;

    @Option(names={"-w", "--watch"}, description="Watch for changes and re-render if .adoc file changes")
    private boolean watch;

    @Option(names={"--revealjsdir"}, defaultValue = "https://cdn.jsdelivr.net/npm/reveal.js@4.3.1", description = "revealjs directory or base url")
    String revealjsdir;

    @Option(names = "--verbose")
    boolean verbose;

    private Asciidoctor asciidoctor;

    public static void main(String... args) {
       System.exit(new CommandLine(new adoc2reveal()).execute(args));
    }

    void verbose(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    @Override
    public Integer call() throws Exception {
        asciidoctor = Asciidoctor.Factory.create();

        asciidoctor.registerLogHandler(new LogHandler() {
            @Override
            public void log(LogRecord logRecord) {
                System.out.println(logRecord.getMessage());
            }
        });
        asciidoctor.requireLibrary("asciidoctor-revealjs");
        asciidoctor.requireLibrary("asciidoctor-diagram");

        render();

        if(watch) {
            WatchService ws = FileSystems.getDefault().newWatchService();

            if(file.canRead()) {
                Path p2watch = file.getAbsoluteFile().getParentFile().toPath();
                System.out.println("Watching files in " + p2watch);
                p2watch.register(ws, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                WatchKey wkey;
                while ((wkey = ws.take()) != null) {
                    for (WatchEvent<?> event : wkey.pollEvents()) {
                       Path p = (Path) event.context();
                       verbose(p + " " + event.kind());
                       if(p.getFileName().toString().endsWith(".adoc")) {
                           render();
                       } else {
                           verbose("Skipping " + p.getFileName().toString() + " as no match.");
                       }
                    }
                    wkey.reset();
                }
            } else {
                System.err.println("Could not read " + file);
            }
        }

        return 0;
    }

    private void render() {
        System.out.printf("Render %s\n",file);

        asciidoctor.convertFile(file,
                Options.builder()
                        .backend("revealjs")
                        .safe(SafeMode.UNSAFE)
                        .attributes(
                                Attributes.builder()
                                        .attribute("revealjsdir", revealjsdir)
                                        .build()
                        ).build()
        );
    }
}
