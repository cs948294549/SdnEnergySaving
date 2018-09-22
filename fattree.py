from mininet.topo import Topo
import logging
import os

# logging.basicConfig(filename='./fattree.log', level=logging.DEBUG)
# logger = logging.getLogger(__name__)

class FatTree( Topo ):
    def __init__(self):
        Topo.__init__(self)
        h1=self.addHost('Host1')
        h2=self.addHost('Host2')
        h3=self.addHost('Host3')
        h4=self.addHost('Host4')
        h5=self.addHost('Host5')
        h6=self.addHost('Host6')
        h7=self.addHost('Host7')
        h8=self.addHost('Host8')
        
        s1=self.addSwitch('Switch1')
        s2=self.addSwitch('Switch2')
        s3=self.addSwitch('Switch3')
        s4=self.addSwitch('Switch4')
        s5=self.addSwitch('Switch5')
        s6=self.addSwitch('Switch6')
        s7=self.addSwitch('Switch7')
        s8=self.addSwitch('Switch8')
        s9=self.addSwitch('Switch9')
        s10=self.addSwitch('Switch10')
        s11=self.addSwitch('Switch11')
        s12=self.addSwitch('Switch12')
        
        self.addLink(h1,s9)
        self.addLink(h2,s9)
        self.addLink(h3,s10)
        self.addLink(h4,s10)
        self.addLink(h5,s11)
        self.addLink(h6,s11)
        self.addLink(h7,s12)
        self.addLink(h8,s12)
        self.addLink(s1,s5)
        self.addLink(s1,s7)
        self.addLink(s2,s5)
        self.addLink(s2,s7)
        self.addLink(s3,s6)
        self.addLink(s3,s8)
        self.addLink(s4,s6)
        self.addLink(s4,s8)
        self.addLink(s5,s10)
        self.addLink(s5,s9)
        self.addLink(s6,s10)
        self.addLink(s6,s9)
        self.addLink(s7,s11)
        self.addLink(s7,s12)
        self.addLink(s8,s11)
        self.addLink(s8,s12)
topos = { 'fattree' : ( lambda k : FatTree(k)) }

