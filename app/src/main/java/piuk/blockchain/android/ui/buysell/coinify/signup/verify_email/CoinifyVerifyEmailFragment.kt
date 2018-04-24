package piuk.blockchain.android.ui.buysell.coinify.signup.verify_email

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_coinify_verify_email.*
import piuk.blockchain.android.R
import piuk.blockchain.android.injection.Injector
import piuk.blockchain.android.ui.buysell.coinify.signup.CoinifySignupActivity
import piuk.blockchain.androidcoreui.ui.base.BaseFragment
import piuk.blockchain.androidcoreui.ui.customviews.ToastCustom
import piuk.blockchain.androidcoreui.utils.extensions.gone
import piuk.blockchain.androidcoreui.utils.extensions.inflate
import piuk.blockchain.androidcoreui.utils.extensions.visible
import timber.log.Timber
import javax.inject.Inject

class CoinifyVerifyEmailFragment: BaseFragment<CoinifyVerifyEmailView, CoinifyVerifyEmailPresenter>(), CoinifyVerifyEmailView {

    @Inject
    lateinit var presenter: CoinifyVerifyEmailPresenter

    init {
        Injector.INSTANCE.presenterComponent.inject(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = container?.inflate(R.layout.fragment_coinify_verify_email)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verifyIdentificationButton.setOnClickListener { onStartCreateAccountCompleted() }

        verifyEmailTermsText.setOnClickListener { openCoinifyTerms() }

        verifyEmailTerms.setOnCheckedChangeListener { buttonView, isChecked ->
            verifyIdentificationButton.isEnabled = isChecked
            if (isChecked) {
                verifyIdentificationButton.alpha = 1.0f
            } else {
                verifyIdentificationButton.alpha = 0.5f
            }
        }

        verifyEmailTerms.isChecked = false
        verifyIdentificationButton.isEnabled = false
        verifyIdentificationButton.alpha = 0.5f

        onViewReady()
    }

    private fun broadcastIntent(action: String) {
        activity?.run {
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(action))
        }
    }

    override fun onStartCreateAccountCompleted() {
        broadcastIntent(CoinifySignupActivity.ACTION_NAVIGATE_CREATE_ACCOUNT_COMPLETED)
    }

    override fun onShowVerifiedEmail(emailAddress: String) {

        verifyEmailTitle.text = getString(R.string.buy_sell_verified_email_title)
        verifyEmailMessage2.text = getString(R.string.buy_sell_verified_email_message, getString(R.string.coinify))

        verifiedEmailAddress.text = emailAddress
        verifiedEmailAddress.visible()
        verifyEmailMessage1.gone()
        verifyEmailAddress.gone()
        verifyEmailOpenEmail.gone()
    }

    override fun onShowUnverifiedEmail(emailAddress: String) {

        verifyEmailTitle.text = getString(R.string.buy_sell_unverified_email_title)
        verifyEmailAddress.text = emailAddress

        verifiedEmailAddress.gone()
        verifyEmailAddress.visible()
        verifyEmailOpenEmail.visible()
    }

    override fun onShowErrorAndClose() {
        ToastCustom.makeText(activity, getString(R.string.unexpected_error),
                ToastCustom.LENGTH_SHORT, ToastCustom.TYPE_ERROR)
        activity?.finish()
    }

    private fun openCoinifyTerms() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(COINIFY_TERMS_LINK)))
        } catch (e: ActivityNotFoundException) {
            Timber.e(e)
        }
    }

    override fun createPresenter() = presenter

    override fun getMvpView() = this

    companion object {

        private const val COINIFY_TERMS_LINK = "https://coinify.com/legal/"

        @JvmStatic
        fun newInstance(): CoinifyVerifyEmailFragment {
            return CoinifyVerifyEmailFragment()
        }
    }
}