package nl.pindab0ter.edinburghinternationalfilmfestival

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle

class RatingDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.rate_and_share_title)
            .setMessage(String.format(getString(R.string.rate_and_share_message), arguments?.get(DIALOG_TITLE) ?: getString(R.string.this_event)))
            .setView(activity.layoutInflater.inflate(R.layout.dialog_fragment_rating, null))
            .setPositiveButton(R.string.share) { dialog, which ->
                TODO("Share rating")
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .create()

    companion object {
        const val DIALOG_TITLE = "title"
        const val DIALOG_TAG = "rating_dialog_fragment"
    }
}