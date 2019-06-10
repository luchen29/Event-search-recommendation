package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			//如果exist，把这个session截取出来
			//如果这里是false代表之前没有cookie check有没有session存在 需要dopost方法
			//false代表不会重新创建session，直接从里面拿出来userid
			HttpSession session = request.getSession(false);
			JSONObject obj = new JSONObject();
			if (session != null) {
				String userId = session.getAttribute("user_id").toString();
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} else {
				//没有得到session login失败的意思
				obj.put("status", "Invalid Session");
				response.setStatus(403);
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}

	}
	//成功登陆之后 就只做doget就行了
	//这个功能作用是保证已经login之后，就算关掉网页 就不会让你再次输入信息了
	//一般网页刚刚login 之后 不会让用户重复输入id和password了。
	//session id 在client端； session在server端

	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");
			
			JSONObject obj = new JSONObject();
			//如果判断成功authenticate
			if (connection.verifyLogin(userId, password)) {
				//则创建session 重新allocate一个新的session id？？ 
				//在memory heap中allocate空间/有些在memory中，有些存储在disk上面
				//这里getSession()里面为空，代表default是true 如果有就提取；没有就创建？？？？？
				//每个client代表一个browser；需要重新创建session
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				//一定时间后自动登出
				session.setMaxInactiveInterval(600);
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} else {
				//对应用户不存在，用户名密码错误等问题
				obj.put("status", "User Doesn't Exist");
				response.setStatus(401);
			}
			RpcHelper.writeJsonObject(response, obj);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}

	}

}
