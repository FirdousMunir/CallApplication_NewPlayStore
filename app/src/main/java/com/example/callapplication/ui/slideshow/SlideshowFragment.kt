package com.example.callapplication.ui.slideshow

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.callapplication.R

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
//        slideshowViewModel =
//                ViewModelProviders.of(this).get(SlideshowViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        val textView = root.findViewById<TextView>(R.id.text_privacyPolicy)

        val str =
            "<h1><b>Privacy Policy</b></h1><br><p>Integrated IT Solutions built the Phone Call Reminder app. This SERVICE is provided by Integrated IT Solutions at no cost and is intended for use as is.</p>" +
                    "<p>This page is used to inform visitors regarding our policies with the collection, use, and disclosure of Personal Information if anyone decided to use our Service.</p>" +
                    "<p>If you choose to use our Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that we collect is used for providing and improving the Service. We will not use or share your information with anyone except as described in this Privacy Policy.</p>" +
                    "<p>The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at call identifier unless otherwise defined in this Privacy Policy.</p><br>" +
                    "<h1><b>Information Collection and Use</b></h1><br><p>For a better experience, while using our Service, we may require you to provide us with certain personally identifiable information, including but not limited to contacts list, calendar, internet. The information that we request will be retained by us and used as described in this privacy policy.</p>" +
                    "<p>The app does use third party services that may collect information used to identify you.</p><p>Link to privacy policy of third party service providers used by the app</p>" +
                    "<ul><li>Google Play Services</li></ul><br><h1><b>Log Data</b></h1><p><br>We want to inform you that whenever you use our Service, in a case of an error in the app we collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing our Service, the time and date of your use of the Service, and other statistics.</p><br>" +
                    "<h1><b>Cookies</b></h1><p><br>Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.</p>" +
                    "<p>This Service does not use these “cookies” explicitly. However, the app may use third party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.</p><br>" +
                    "<h1><b>Service Providers</b></h1><p><br>We may employ third-party companies and individuals due to the following reasons:\nTo facilitate our Service;\nTo provide the Service on our behalf;\nTo perform Service-related services; or\nTo assist us in analyzing how our Service is used.</p>" +
                    "<p>We want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.</p><br>" +
                    "<h1><b>Security</b></h1><p><br>We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and we cannot guarantee its absolute security.</p><br>" +
                    "<h1><b>Links to Other Sites</b></h1><p><br>This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by us. Therefore, we strongly advise you to review the Privacy Policy of these websites. We have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.</p><br>" +
                    "<h1><b>Children’s Privacy</b></h1><p><br>These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case we discover that a child under 13 has provided us with personal information, we immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us so that we will be able to do necessary actions.</p><br>" +
                    "<h1><b>Changes to This Privacy Policy</b></h1><p><br>We may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. We will notify you of any changes by posting the new Privacy Policy on this page.</p><p>This policy is effective as of 2021-03-01</p><br>" +
                    "<h1><b>Contact Us</b></h1><p><br>If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact us at info@integrated-itsolutions.com</p>"

        textView.text = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT)

        return root
    }
}