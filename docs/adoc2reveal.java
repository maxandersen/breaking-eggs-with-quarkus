///usr/bin/env jbang "$0" "$@" ; exit $?
/* can run faster this way - but not a requirement */
//JAVA 17+
//JAVA_OPTIONS --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED
//DEPS org.asciidoctor:asciidoctorj:2.5.3
//DEPS org.asciidoctor:asciidoctorj-diagram:2.2.1

////REPOS mavencentral,jitpack
////DEPS com.github.asciidoctor:asciidoctorj-reveal.js:main-SNAPSHOT

//REPOS mavencentral,https://oss.sonatype.org/content/repositories/orgasciidoctor-1289
//DEPS org.asciidoctor:asciidoctorj-revealjs:5.0.0.rc1


import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.log.LogHandler;
import org.asciidoctor.log.LogRecord;

import java.io.File;

public class adoc2reveal {

    public static void main(String... args) {
        File file = args.length>1 ? new File(args[0]) : new File("index.adoc");
        
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();

        asciidoctor.registerLogHandler(new LogHandler() {
            @Override
            public void log(LogRecord logRecord) {
                System.out.println(logRecord.getMessage());
            }
        });
        asciidoctor.requireLibrary("asciidoctor-revealjs");
        asciidoctor.requireLibrary("asciidoctor-diagram");
        asciidoctor.convertFile(file,
                Options.builder()
                        .backend("revealjs")
                        .safe(SafeMode.UNSAFE)
                        .attributes(
                                Attributes.builder()
                                        .attribute("revealjsdir", "https://cdn.jsdelivr.net/npm/reveal.js@4.3.1")
                                        .build()
                        ).build()
        );
    }
}
