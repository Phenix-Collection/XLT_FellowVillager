package  com.xianglin.fellowvillager.app.model;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 针对sdatajson格式
 * @author wanglei
 *
 */
public class JsonBody implements Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = -418668496191635944L;

	private Long sKey;

	private Integer sOpCode;

	private SData sData;

	public Long getsKey() {
		return sKey;
	}

	public void setsKey(Long sKey) {
		this.sKey = sKey;
	}

	public Integer getsOpCode() {
		return sOpCode;
	}

	public void setsOpCode(Integer sOpCode) {
		this.sOpCode = sOpCode;
	}

	public SData getsData() {
		return sData;
	}

	public void setsData(SData sData) {
		this.sData = sData;
	}

	public static void main(String[] args) {


		Md md = new Md();
		md.setFromid("10444");
		md.setToid("212321");
		md.setMessage("sdsd");

		SData data = new SData();
		data.setBiz("chart");
		data.setMd(md);

		JsonBody body = new JsonBody();
		body.setsKey(2321321321L);
		body.setsOpCode(23213);
		body.setsData(data);
		System.out.println(body);

		body = JSON.parseObject("{\"sKey\":0,\"sOpCode\":\"1001\",\"sData\":null}", JsonBody.class);
		System.out.println(body);

	}



}