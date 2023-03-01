import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ValorantSkins {
    private JFrame frame;
    private JList<String> skinsList;
    private ArrayList<Skin> skins = new ArrayList<>();

    public ValorantSkins() {
        frame = new JFrame("Valorant Skins");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultListModel<String> skinsModel = new DefaultListModel<>();
        skinsList = new JList<>(skinsModel);
        skinsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        skinsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = skinsList.getSelectedIndex();
                if (index >= 0) {
                    Skin skin = getSkin(index);
                    showSkinInfo(skin);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(skinsList);
        frame.getContentPane().add(scrollPane, BorderLayout.WEST);
        frame.setVisible(true);

        loadData();
    }

    private void loadData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://valorant-api.com/v1/weapons/skins")
                .build();
    
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Gson gson = new Gson();
            SkinList skinList = gson.fromJson(responseBody, SkinList.class);
            skins = skinList.data;
            DefaultListModel<String> skinsModel = (DefaultListModel<String>) skinsList.getModel();
            skinsModel.clear();
            for (Skin skin : skins) {
                skinsModel.addElement(skin.displayName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

private Skin getSkin(int index) {
    return skins.get(index);
}

    private void showSkinInfo(Skin skin) {
        String message = "Display Name: " + skin.displayName + "\n" +
                "Display Icon: " + skin.displayIcon + "\n" +
                "Cost: " + skin.shopData.cost.amount + " " + skin.shopData.cost.currency + "\n" +
                "Is Featured: " + skin.shopData.isFeatured + "\n" +
                "Is Promo: " + skin.shopData.isPromo + "\n" +
                "Video URL: " + skin.videoURL;
        JOptionPane.showMessageDialog(frame, message, "Skin Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class SkinList {
        ArrayList<Skin> data;
    }

    private static class Skin {
        String displayName;
        String displayIcon;
        ShopData shopData;
        String videoURL;
    }

    private static class ShopData {
        ShopCost cost;
        boolean isFeatured;
        boolean isPromo;
    }

    private static class ShopCost {
        int amount;
        String currency;
    }

    public static void main(String[] args) {
        new ValorantSkins();
    }
}