package com.gamesofni.neko.guesswhichsaint.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamesofni.neko.guesswhichsaint.R;
import com.gamesofni.neko.guesswhichsaint.data.Saint;
import com.gamesofni.neko.guesswhichsaint.db.SaintsQuery;

import static com.gamesofni.neko.guesswhichsaint.utils.Utils.checkSupportedAction;
import static com.gamesofni.neko.guesswhichsaint.utils.Utils.showClientsMsg;
import static com.gamesofni.neko.guesswhichsaint.utils.Utils.showNoAppClientsMsg;

public class SaintInfo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saint_info);

        Intent intentThatStartedThisActivity = getIntent();
        if (!intentThatStartedThisActivity.hasExtra(Intent.EXTRA_UID)) {
            return;
        }

        final long saintId = intentThatStartedThisActivity.getLongExtra(Intent.EXTRA_UID, -1L);
        if (saintId == -1L) {
            return;
        }

        TextView saint_name = findViewById(R.id.saint_name);
        TextView saint_description = findViewById(R.id.saint_description);
        TextView saint_attributes = findViewById(R.id.saint_attributes);
        ImageView saint_icon = findViewById(R.id.info_saint_icon);

        Saint saint = SaintsQuery.getSaint(this, saintId);

        if (saint == null) {
            return;
        }

        saint_name.setText(saint.getName());
        saint_description.setText(saint.getInfo());
        saint_icon.setImageDrawable(saint.getIcon());

        final SpannableStringBuilder attributes = new SpannableStringBuilder(getString(R.string.saint_info_attributes_list_prefix) + TextUtils.join(", ", saint.getAttributes()));
        attributes.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        saint_attributes.setText(attributes);

        final String wikiUrl = saint.getWikiUrl();
        Button wikiButton = findViewById(R.id.wiki_article_button);
        wikiButton.setOnClickListener(v -> openBrowserLink(v, wikiUrl));

    }

    private void openBrowserLink(View v, String pageUrl) {
        Uri page = Uri.parse(pageUrl);
        Intent webViewIntent = new Intent(Intent.ACTION_VIEW, page);

        checkSupportedAction(this, v, webViewIntent, getString(R.string.unsupported_action_name_browser));

        try {
            startActivity(webViewIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            if (page == null || page.toString().isEmpty()) {
                showClientsMsg(this, v, null);
            } else {
                showNoAppClientsMsg(this, v, getString(R.string.unsupported_action_name_browser));
            }
        }
    }
}
