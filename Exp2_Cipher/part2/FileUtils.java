package Exp2_Cipher.part2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
   public static void ensureDir(String path) throws IOException {
      Files.createDirectories(Paths.get(path));
   }

   public static void write(String path, String content) throws IOException {
      try (BufferedWriter w = Files.newBufferedWriter(Paths.get(path))) {
         w.write(content);
      }
   }

   public static void append(String path, String content) throws IOException {
      try (BufferedWriter w = Files.newBufferedWriter(Paths.get(path),
            java.nio.file.StandardOpenOption.CREATE,
            java.nio.file.StandardOpenOption.APPEND)) {
         w.write(content);
      }
   }
}