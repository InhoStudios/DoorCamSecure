package net.inhostudios.doorcam.image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import net.inhostudios.doorcam.Globals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ImageHandler {

    private String imageFilePath = Globals.resources + "\\Me.jpeg";

    public static ArrayList<String> detectLabels(String filePath) throws Exception, IOException {
        PrintStream out = System.out;
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ArrayList<String> results = new ArrayList();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION))
                        .setImage(img)
                        .build();
        requests.add(request);

        // Perform the request
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            // Display the results
            for (AnnotateImageResponse res : responses) {
                for (LocalizedObjectAnnotation entity : res.getLocalizedObjectAnnotationsList()) {
                    results.add(entity.getName());
//                    out.format("Object name: %s\n", entity.getName());
//                    out.format("Confidence: %s\n", entity.getScore());
//                    out.format("Normalized Vertices:\n");
//                    entity
//                            .getBoundingPoly()
//                            .getNormalizedVerticesList()
//                            .forEach(vertex -> out.format("- (%s, %s)\n", vertex.getX(), vertex.getY()));
                }
            }
        }

        return results;
    }

}
