package com.adib.githubuser

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FollowingFragment : Fragment() {

    private val listFollowing = ArrayList<User>()
    private lateinit var adapter: FollowingAdapter
    private lateinit var pbFollowing: ProgressBar
    private lateinit var rvFollowing: RecyclerView
    private lateinit var tvNoFollowing: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
        const val DETAIL_USER = "detail_user"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FollowingAdapter(listFollowing)
        pbFollowing = view.findViewById(R.id.pb_following)
        rvFollowing = view.findViewById(R.id.rv_following)
        tvNoFollowing = view.findViewById(R.id.tv_no_following)

        listFollowing.clear()
        val following = activity!!.intent.getParcelableExtra(DETAIL_USER) as User
        getUser(following.username.toString())
    }

    private fun getUser(id: String) {
        pbFollowing.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$id/following"
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    if (jsonArray.length() < 1) {
                        tvNoFollowing.visibility = View.VISIBLE
                        pbFollowing.visibility = View.INVISIBLE
                    }
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val username: String = jsonObject.getString("login")
                        getUserDetail(username)
                    }
                }
                catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                pbFollowing.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Log.e(TAG, errorMessage)
            }
        })
    }

    private fun getUserDetail(id: String) {
        pbFollowing.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$id"
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val avatar: String = jsonObject.getString("avatar_url").toString()
                    val username: String = jsonObject.getString("login").toString()
                    val name: String = jsonObject.getString("name").toString()
                    val location: String = jsonObject.getString("location").toString()
                    val repository: Int = jsonObject.getInt("public_repos")
                    val company: String = jsonObject.getString("company").toString()
                    val followers: Int = jsonObject.getInt("followers")
                    val following: Int = jsonObject.getInt("following")

                    listFollowing.add(
                        User(
                            avatar,
                            username,
                            name,
                            location,
                            repository,
                            company,
                            followers,
                            following
                        ))
                    showRecyclerList()
                }
                catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                pbFollowing.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Log.e(TAG, errorMessage)
            }
        })
    }

    private fun showRecyclerList() {
        rvFollowing.visibility = View.VISIBLE
        rvFollowing.layoutManager = LinearLayoutManager(activity)
        val followingAdapter = UserAdapter(listFollowing)
        rvFollowing.adapter = followingAdapter

        followingAdapter.setOnItemClickCallback(object:
            FollowingAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {}
        })
        pbFollowing.visibility = View.INVISIBLE
    }
}