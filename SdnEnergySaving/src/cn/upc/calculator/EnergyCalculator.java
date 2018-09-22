package cn.upc.calculator;

/**
 * 通用能耗模型
 * 
 * 已有研究表明 [96] ：当前模块化网络设备的能耗主要取决于设备配置和经 过它的网络流量速率，其中网络设备配置包括：交换背板类型、线卡类型、
 * 线卡数量以及网络接口配置速率等。
 * 
 * @author 江荣展
 *
 */
public interface EnergyCalculator<C, T> {
	/**
	 * P(C,T)=F(C)+Q(C)+H(C,T)
	 * 
	 * @param c
	 *            网络设备的配置
	 * @param t
	 *            经过它的网络流量速率
	 * @return P(C,T):一台网络设备的总能耗
	 */
	public int p(C c, T t);

	/**
	 * 包含设备背板能耗、散热系统能耗、转发查找等计算能耗开销以及TCAM和RAM等存储能耗开销等
	 * 
	 * @param c
	 *            网络设备的配置
	 * @return F(C):固有能耗开销
	 */
	public int f(C c);

	/**
	 * 该能耗值不包括线卡上网络接口的能耗，因此只与线卡的类型和数量有关
	 * 
	 * @param c
	 *            网络设备的配置
	 * @return Q(C):网络线卡总耗能
	 */
	public int q(C c);

	/**
	 * H(C,T)=SUM(N*(B(C)-U(C,T))
	 * 
	 * 该能耗值与网络接口配置和经过接口的网络流速率相关
	 * 
	 * @param c
	 *            网络设备的配置
	 * @param t
	 *            经过它的网络流量速率
	 * @return H(C,T):该设备中所有网络接口的总能耗
	 */
	public int h(C c, T t);

	/**
	 * 网络接口的能耗随着经过它的网络流传输速率的减少而降低
	 * 
	 * @param c
	 *            网络设备的配置
	 * @return B(C):网络接口在满利用率时的能耗
	 */
	public int b(C c);

	/**
	 * 
	 * @param c
	 *            网络设备的配置
	 * @param t
	 *            经过它的网络流量速率
	 * @return U(C,T):网络流量速率变化所产生的网络接口能耗差异
	 */
	public int u(C c, T t);
}
