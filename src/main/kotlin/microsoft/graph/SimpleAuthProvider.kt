package microsoft.graph

import com.microsoft.graph.authentication.IAuthenticationProvider
import com.microsoft.graph.http.IHttpRequest

/**
 * microsoft.graph.SimpleAuthProvider
 */
class SimpleAuthProvider(private val accessToken: String?) : IAuthenticationProvider {
    override fun authenticateRequest(request: IHttpRequest) {
        // Add the access token in the Authorization header
        request.addHeader("Authorization", "Bearer $accessToken")
    }
}