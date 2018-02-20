package nl.pindab0ter.edinburghinternationalfilmfestival

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.widget.RatingBar
import nl.pindab0ter.edinburghinternationalfilmfestival.utilities.starRatingFor

class RatingDialogFragment : DialogFragment() {
    private lateinit var alertDialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        alertDialog = AlertDialog.Builder(context!!)
                .setTitle(R.string.rate_and_share_title)
                .setMessage(String.format(getString(R.string.rate_and_share_message), arguments?.get(DIALOG_TITLE) ?: getString(R.string.this_event)))
                .setView(activity.layoutInflater.inflate(R.layout.dialog_fragment_rating, null))
                .setPositiveButton(R.string.share) { dialog, _ ->
                    val title = arguments?.get(DIALOG_TITLE)
                    val website = arguments?.get(DIALOG_WEBSITE)
                    val rating = starRatingFor((dialog as AlertDialog).findViewById<RatingBar>(R.id.rating_bar).rating)
                    val shareText = String.format(getString(R.string.share_text_with_rating), title, rating, website)

                    startActivity(Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    })
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        return alertDialog
    }

    override fun onResume() = with(alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)) {
        super.onResume()

        isEnabled = false

        alertDialog.findViewById<RatingBar>(R.id.rating_bar).setOnRatingBarChangeListener { _, _, _ ->
            isEnabled = true
        }
    }

    companion object {
        const val DIALOG_TITLE = "dialog_title"
        const val DIALOG_WEBSITE = "dialog_page_url"
        const val DIALOG_TAG = "rating_dialog_fragment"
    }
}