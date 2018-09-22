package user.mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import net.floodlightcontroller.packet.Ethernet;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFType;
import org.openflow.util.HexString;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

public class MACTracker implements IOFMessageListener, IFloodlightModule {
	/**
	 * 由于要监听openflow消息，所以要向FloodlightProvider 注册。
	 */
	protected IFloodlightProviderService floodlightProvider;
	/**
	 * 同时需要一个集合变量macAddresses 来存放控制器发现的MAC地址。
	 */
	protected Set<Long> macAddresses;
	/**
	 * 最终，需要一个记录变量logger来输出发现过程中的记录信息。
	 */
	protected static Logger logger;

	/**
	 * 把依赖关系告诉模块加载系统 编写模块加载代码。 通过完善getModuleDependencies()
	 * 告知加载器在floodlight启动时将自己加载。
	 */
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> c = new ArrayList<Class<? extends IFloodlightService>>();
		c.add(IFloodlightProviderService.class);
		return c;
	}

	/**
	 * 这个方法会在controller启动时调用，以加载依赖和数据结构
	 * 创建Init方法，Init（）将在控制器启动初期被调到，其主要功能是加载依赖关系并初始化数据结构。
	 */
	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		macAddresses = new ConcurrentSkipListSet<Long>();
		logger = LoggerFactory.getLogger(MACTracker.class);
	}

	/**
	 * 为PACKET_IN消息绑定事件处理委托，在这之前我们必须保证所有依赖的模块已经初始化
	 * 在实现基本监听功能时，packet-in消息需在startup方法中被记录和注册，同时确认新增模块需要依赖的其他模块已被正常初始化。
	 */
	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

	/**
	 * 为OFMessage监听器添加ID
	 */
	@Override
	public String getName() {
		return "MACTracker"; // 或 return MACTracker.class.getSimpleName();
	}

	/**
	 * 为PACKET_IN事件处理程序添加实现代码，该方法返回Command.
	 * CONTINUE以便Ifloodprovider能够将Packin消息发往下一个模块，其它事件处理程序继 续处理。
	 */
	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		Long sourceMACHash = Ethernet.toLong(eth.getSourceMACAddress());
		if (!macAddresses.contains(sourceMACHash)) {
			macAddresses.add(sourceMACHash);
			logger.info(" MACTracker消息：MAC Address: {} seen on switch:{}", HexString.toHexString(sourceMACHash), sw.getId());
		}
		return Command.CONTINUE;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		return null;
	}

}
