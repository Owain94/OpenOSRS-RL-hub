/*
 * Copyright (c) 2020, ThatGamerBlue <thatgamerblue@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.thatgamerblue.runelite.plugins.rsnhider;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptID;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import java.util.Random;
import org.pf4j.Extension;

/*
Mental breakdown 2: electric boogaloo

Alexa, play sea shanty two.
Peace to:
	r189, he.cc
*/
@Extension
@PluginDescriptor(
	name = "RSN Hider",
	description = "Hides your rsn for streamers.",
	tags = {"twitch"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class RsnHiderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private RsnHiderConfig config;

	private static String fakeRsn;

	private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	@Provides
	private RsnHiderConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RsnHiderConfig.class);
	}

public static String[] NAMES = {"immygpimp", "pistcuettyple", "edi wator", "Doe158", "SGHAV3", "The hellooser", "Roup", "AVasTyler", "chetrivris", "Facejector5", "InternetYusNoadforne2", "DJBattyster99", "LotIsnier", "vomazeutta", "adaxtare7", "anchess rx", "Handenid", "skepthsr", "latecocks", "The Grutic", "TheMightXates", "Kix0Paphonia", "ElwettickFeng", "stickin23", "yousismchair", "thefunkalk1", "batgopper", "tereoordones", "Glashfar", "shadow throid", "isalomankativechampi", "k4rm", "Shiggyantza", "PM MROSESDSBAT", "Snuwing", "Rykal431", "Mumashwit", "The arayophelphill", "justthecommandagoll", "theclamood", "ittovan mithin", "Psrof3s", "Forden Lost FlowIy", "kblragg", "inico1221", "starson a monalah", "Eles phence ube", "YouYoutPossCifat", "loghbasdobagant", "ThisDillingWhite", "JamicakMefass", "Madcheewingcatua", "HombaloWengOnYous", "Bearsh", "TakZ35", "Bacendaul", "Low Vits", "psproocoldbecozish", "Lordjemfer", "Aqimlobhdice", "sam23217", "ChaozaOwn", "hamballtrombsed", "cimertruster", "masis sprofcont", "Moonme SunDamisti", "ktimjulskips", "streverdefist", "Dernout", "its staffen899", "betanderthd", "fm8717", "i rever", "KellowPosty", "Kindopee747", "lightbead", "SkYMG", "haverockel8908", "casu0st", "explesical", "Wilin1140", "Xefg1980", "the grife exetted", "Statt01357", "Fldsangurnt count6", "Shammer1956", "DBa SeAD", "baninassimmydel", "RednaxX", "BettyConate", "PoldoSnifeWuppies", "fufkinsaffe", "rowstchesselfpucksonx", "epcoist1987", "xeayepplyX", "dallmillian", "KY Crizard", "The Buds", "JimRZainCharopo", "Encertrum", "Colfuck offace", "Bnarliguss", "Hot Have Morn", "ineryburgayoak", "mr man peetere", "goltheyess", "LaumseYou", "themcfortry42", "jcm66", "BrefSnapfulet", "ducksteder1", "HereymcDet", "RuiatioAm51", "HandlerkDruntum", "FodeYN", "BanHassHoads", "baruanc216", "beto b990", "o", "lossjam", "JaqEG racal7", "myLoveDoRin", "Cotch", "mastenstabLAMON", "Paxi Kids69", "ChrisInAnglyNeel", "ol ivan", "Whonezelf02", "Panda Cool99", "Qingersgrees", "MadganThatharth", "jedkelxon", "isemper2", "Daralwalf", "InsanchyRank", "Britzesh", "HoVescrineerteAsexis", "jomeisdeadbaron", "IgusticUtman", "tlairedark1999", "DepmeticBragon", "galotickus", "BotSevenkaticward", "migi1666", "LungNacks151", "complehonshinasu", "yhactio", "raovalu", "TromeFighty", "GarofBlugDeTacro", "Zeef LK", "jgshour", "ImYountChris25", "Laydiv", "DrawnOloonPecks", "sziebox", "Tg2Bashaouz", "holegood430", "Samurrious", "RSBLWLC", "brolegolla", "magicam", "BVS56", "Deezzygoot77", "arganja", "Bigbugy", "strage08", "dyok1238", "itsymmath", "secfornet", "Gharninlenn", "butthabium", "dwebde", "fto", "sysypster", "Karelle", "hiebooman80", "Dimarajesis", "Aghtnity", "scc9825", "Naixa", "ilifffishkilt", "ChuddhesMD7", "FlC42309", "maxisnagitethepash", "cs yuusha", "bwystropheloman", "seebie61", "TheBibBumbie", "therecoberdompran", "TobaBucklinx", "nulkerb77", "acd96", "neogerbar", "Nozer Me Here", "cheekybree", "ds chinisfous", "theturtat62", "thejalfell", "nishpirg", "Speptheone183", "ecitz", "FizartDi1g", "cennylomeistis", "axnix1", "Karajo", "DJOCOPE DAV00", "ytzt", "danner157sx", "axel courtrum", "t95", "thorsago", "flrrnrn e", "ceterman6000", "Ohnier25", "thunduger", "PothamJah", "Yyriobuivesto", "burrettheaeliath", "Bualoh mira its hucker1fStool", "lts mungle", "atrapliktal", "matkarwh3484", "zapenjest", "YeonZumpers", "Aploop89", "Othrell", "AndreX", "1suptreid", "phrosmflap", "Enamansafilotivm", "allun", "willsceched", "dunixa1099", "Deskra", "sammt3", "myMakeJiahso", "someishspinfirafus", "Creatingtamana123", "St26540", "ConnamoneMidey", "Ginal  itpean", "TheMasterlopsticks", "MostLoller86", "Shanken Pirate", "Ainwerd", "bausernakot14", "Goopydetthind", "dead4skills4", "screthictreopyy", "VinNahCoch", "Gilltuter031", "lord live captain", "SucSunnator", "PaliceKulipa9", "Ooper McBesty", "Asker27", "gicked651", "realhyquintro", "better fumples", "the bennah", "teblabutter2", "twrmdz05", "ILiFK", "kyonald eer", "thenughmilsewarp", "ComlemNobber4Soxxx", "TheGlober", "dt0kens123", "Solyistini", "chew423", "WellSpunVet", "taulati", "sexionanina", "Neo30183", "Bullmoranabla", "dankmaft", "LarthCow", "Mike3337", "beefjuady", "HumooonB1db", "pkin017", "capty crickiong", "Carlad0t", "taracid", "6hidaosidiappussopassian", "hive66662", "lorecrazy", "AvenHexAt", "Robosevian90", "kenbeini", "aamyamproged", "MrStefermo", "c0br32p305", "Hell McBlack9012", "pigercky55", "DighSavurug", "dresroy", "TurblyBnee", "amanja13", "Samgision", "FOLS2", "dombbr1171", "challeedatpavagul", "KingerThass", "SAustaderman", "Ad emo", "KikaZayray", "Apararom", "Chocotions", "Lone Altai", "Ctoolstrice002", "Essaualmybear", "airinjabot", "WhitAXlightlyPolk", "ALI 7 X0x", "PDEUR", "boohorm20f", "ujacanjam", "1il2550", "Asshin cake", "darfle 21", "yourmanessionbarrer", "guicommerc", "Herralm82", "southkid94", "BlunchJuscas", "BigWhatUsAn", "Acesoftacat", "butswins", "DrTollemer", "TheCheesitie", "Lanedonpai", "The Blunch Lepti", "auturtassluts", "NagineDaggo", "boodly23", "sweatdead", "Gerolon", "darkhan25", "kenovylabo281", "Themony885", "SirLessureCurtMes", "Swankzerdants", "FillofatzCatWit", "xBook", "Woraltuctorou", "gemaho2", "Runnmansten", "MilkRbo", "EprainsK", "LisholeZamilla", "lyve177", "DavilTeppons", "gettensubread", "SupaiousWith", "woontallow", "riborttim", "chipa gouse", "XcactleznD", "NezoroG", "raybengurr", "QuandyRightman046", "chalkhatt7", "PulteRife", "NeburNotox", "CompleOmoranican", "Komospercy", "Kridzzailut", "Phorolator69", "jjewty1989", "DuderFTYR", "cammah", "booblemanicarman01", "Hatthenpird4", "smart bisco", "Elgelaac", "Basblappi", "OnePixkot", "useln11817", "IsaChinyTurn", "ssypic82", "caibac25611", "phackvaBlacka", "aprainbos", "godstaceman53", "ebbermschow", "mrsmic", "GreyMcThehress", "omiliin12", "crairro76", "SoulssBroicer", "DarthanningSwelvyts", "Diggypho", "therinajables", "whatspaceginyone", "gotc0", "avis011201017", "XXWILI80", "scortyfich", "purati", "Diands", "NotStarmy58", "WeverCatas", "anyemdfwilling", "LurtyMaker1", "toGrancat", "FarthServer", "xXLex Pangus", "Nourforn", "danceris thom", "s2spe264", "miliffifichella", "upnothesz", "yuconncutts", "sweetbag", "gaeayrack", "palpal16", "jahgar426", "TheLumpantic I", "seapthegool", "the leatells", "Nbit9z", "MOkazoo", "RungleRoid", "126K", "HawlooSwacemx", "superzot", "jokerbooky", "JAmashian", "Liluve Varku", "Ugom0nManix", "Jimmytords", "windesterzer3963", "Bigfrophelogicloo", "itspeaching", "subice868", "c0fkbtaur", "ExitHealnisebun", "MarkaGox", "PlainDusck", "Fleesaspersacragger", "FlameEateCumpuser", "LoodMubsdrack", "AserTro0se", "ETW", "rumpyunkon", "TheDogingistus", "gualasausu", "HoteGuyaccm", "cammer86", "ReallyAlocx", "cjducsc", "GoNuta", "stiddard11", "leckraysc", "nt4244", "EsexDuckzoshilicho", "Ohuai", "KaythaShenius", "fain and wade", "sinnyboooro", "tigfucked", "SlattyMcBees", "Bulone", "TheComegain92", "Meantherbird", "erasa Alligal", "ii het poft", "TheRarbosag", "Detoralian", "Averman", "the co a picmacate", "i Buffed HarleckFafSanPoJ", "DY Pepper", "Bubaloller", "jabootdoy", "Milding Lady Br", "todricfu", "Mexygal", "Nickle77", "ruicklight", "PME MOUAY", "Naga d300", "90k550", "digger91", "SpedingPouldJoir", "gwips32", "UTs", "doctormermalic", "Roozle Ruggu", "colonig", "magureafasea", "TheRabyHowinDany", "Sk00pitule", "SmysticEboant", "kerlz", "kyfversd0908", "rusty03010", "AProSnowR42", "jdm112", "Slazyhumo", "SwAmokutka", "fasticabeazlapack", "babboyoik", "IssantmyCoves", "Fdnempsolegaust", "chatnaffel", "NianAndoic", "Kbonaton kude", "ElpelmistCowe121", "zonythements", "ToodooPoker", "alkraver", "Check2 Ouras", "Terriancakes", "gripfalment", "NooneGuitivithetheFo", "Bassedsheep", "KurnShimmery", "Spappyster", "Khopios", "MySexio", "gowelout it", "doltehot1120", "edoman", "jottmes", "jero 1101", "wutjudero", "seccoff", "mjtnqu0ts", "JacksMcLuckle", "progicman", "williebin979", "uchicact98", "hellast26", "LoveOverDogo", "cs68600", "oticwickbii", "sadgeteob", "granginglam", "Zonathast25", "DupFubus419", "minkerydome", "wessafuy", "Obalac", "Avanthroast", "yauratear27", "puncher condred5", "Disaganco3", "WhofGooble", "MeasterChicken", "IblamedosCoolia", "Phhizza Gop", "MacBeller", "danknokmd", "Ma2wolfgood", "RogalDadcorris", "JusttheCommatogirate", "CraDnamano", "peikak", "Zownnijnakai", "ippperiganator", "oldrision", "cat useannmass", "warpustian", "Bluestick 608", "outafinthemy", "Whone stackgorer", "Yunderthedamsone", "cocyslayer", "habars19", "TheALakersPeeche", "Quercoje1060", "riggeric", "bigarilaw", "DKMister", "ipelogsorch", "moggysseak", "Marcinal Actor", "Bignoat house", "funkycle", "Tigmarper", "dudespagmunn", "bananapron", "Shewurper", "BURBigYox", "Morik es ap", "Fankaloo", "Vinzumshat", "nookays", "OneEtEatPorty", "Skyzzrjb", "RIoRizzlyPatama", "jastuch", "Cibanomaker", "noidjohntn", "Ethinothenamethen98", "mumbusidertolent", "Un evthen", "kunker1994", "SinX82", "ZiFrovePynio", "Zamster20", "maldam6895", "novary802", "imtran98", "RustCanders201", "vqapshark112", "Tra   Socious Bout", "beverkeskilly", "RakTheDeche", "oshit", "powertiantbee", "rl3k3c", "GenerS9", "moneybb", "Narpak", "miderburn", "Flustius11", "Rucknowpoming", "steelrunsti11986", "InoguySilon", "Fisesair", "goder1kbea", "yailrouse", "ousthew1", "Gayinfellof", "oshalwynderbro", "MACools offidell", "Dundeh25", "seCQeeverRanSem", "NakingCantrey", "IssItCC", "DBSFbrosco", "BrEXNo TOIN 2WEH MANPICK", "iwfellander", "Dunizz2", "icnixuar00015", "VACHEHCPAT", "addocadrax99", "the beard 1075", "WestafHanta Clid", "SyVemaster", "MarkinSentiey", "landombut", "Cluchinomi67", "toablotoy33", "Zyarthler", "MrSonal", "rihatchus", "capertal", "LansterAnosig", "sampletooz", "Warm10Nom", "Vickezz", "tremph01", "eled kin tsa", "UFz", "PooldtionDirala", "NohratoK", "Garmons", "ForPuckets", "onemanfruppin", "Vaov", "manonecrato", "layleywaee", "Doneyou Chill", "mjk003", "PennyMynattait", "Bubwender benny", "profacamillou", "EvinDF", "Skinkklinds", "Misterslascie", "ATAEDx", "JohnnyJulie74", "Vorerzo", "mimmywardcho", "SiranSticken", "gooding peylig", "Ash taus", "Guynessmitt", "Sbrin rule", "3JohnnyHerfs", "jast151", "gaast ts3", "mikesqatley", "mermon17", "bobbyricks7423", "vaseryra409", "Thispuprighted", "wigzoffie", "noohiepheemers", "Head Master031", "HungeJusterWithTurgio", "cattainhcg", "Zampinstie", "ChammanTh", "AdleDerris", "SupgraTaBiech", "loopbonnerk", "iliveclapboit", "skankit", "hambongk", "SleekLulle1", "ilyoge", "LicidMous", "PooThing65", "Seunding Neblay", "muntalystaulwisha", "yonginderglurd3207", "jrpockins", "sampleboy10", "pui837", "lownNick19701", "illocketc", "bigtwideo", "TGMDMooker", "Yearalgass400", "codes cheaser", "BattandsofCheis", "Beatlock65", "ghemrownomatrabby", "LordofAnStons", "jcaur0s", "Squirrelboys", "nakri", "Haltheshawn", "cogote manc", "Freb im Arginy", "xYottan", "Rangungle2", "boneroregd", "pressvorle", "the boolen", "Imnice of thinker", "silverbie", "Rualmuffon", "FPOND906", "TekerTV", "SheatMech", "Egg20s", "nukoosea", "bdbullate60", "VighingNoCommerto", "KnutatorFucks", "GringinWackingMasane", "todda", "Quadthewrite", "fugitalreef", "slicegamehurdorg", "plornboy", "Brank34", "CleenDiac0", "CuPigViddiggSus", "GASterNusty", "Primnwl", "Yame Chrisactio", "DakarsDGCdester215", "kot wulkerbush", "Maihook", "BJA1995", "Mysponker1", "joeinfust", " Hoode of Bick your", "MrSlutz", "morro99", "Stecept", "AriHamFG", "iVinden74", "DiamBravel", "American373", "GhillViller", "xatan quidman", "MickElp01", "AathualSwarbs", "YnawajaB", "froetdeepers3", "Moohbeespakerpen", "axise226", "kellientyford", "eliquiniped", "mfodrade", "VillurinaWino", "ZembDrank", "barthdog", "InheremChangous", "pabydourfrom", "meillingn", "memejayglas", "VenGrats", "apitEfcromoniz", "WhotRodYingeFone", "SALW", "generspickjokelphap", "ashenti 225", "Impornis420dy", "rolschanson2", "jack as jayye", "Rasion Fl", "Lucspurf", "DuussuSDreadhung", "Silennatson", "hiffbiragydurd", "Nicktystrion", "Squidd3", "paburtlebone", "DeLangus10", "xylent J", "honyflyanturgers", "leartanth", "Qiotently ul", "80yusobraizer", "MadaBThand", "folshesymir", "Chimaih", "Riagor77", "jdeaboss", "203lz8408 2", "th3sk0f", "DonoFoussTeonI1", "Ljtcam", "sickonethundy", "Highe Luda", "bigsleard23", "ReadermatteGuitars", "KaraFoToB", "kigewellofer", "Pickletonor", "musereyed", "jtcetwerry", "favemallirygexi", "Boissia", "Mus1k", "13TxNA", "Malkies", "IzMessist", "ncKRea99", "WurdRedditAsss", "woodilsmube", "Bastegios", "primpteruga", "pictupersiari", "V0gnut", "murpingis", "jeiiko382", "Del1GKLU", "UbaBettLiceOffall", "charlo ele earl", "shawkzoo", "ShwahwlRordan", "NAGGRADZL", "Galeza00073", "LevhigurouX", "Bowand109", "pendyhapmanbus", "swag1c3", "buggy toastle0sacchizle", "onlondeach", "illinvonander", "Littleuser T", "Alacouspearthelecter", "kutatad", "GoodofTheBooFny", "Robong1091", "sipspan", "Flossell pool 423ii", "ThatItuwarz", "moashmandeetree", "HandKictionECJ", "Beverd", "slazhaus", "akoxionn", "GamisWorksAtWhorie", "Phhosho12", "A Dendito10", "dillatablapates", "Acculids", "the john613", "Leman", "PM FlomSidel", "Elphawin565", "nuiventr15", "ScottHyupor", "za36", "RewVaye04", "ruster4tyer", "binhan80", "randward98", "parthanuer", "Rimvkul", "beyraidarksoufs", "NipBonetPoombooter", "dguy", "lightho", "snookskirs", "pilmonster1312", "hoshvorgersus", "lickalivitiko", "DudelScosoin", "shenboomuplow", "moastboy8", "cantremichele", "Chester56", "Inyhemant", "ekndwyguh", "Ginfadiots", "The Ekennath", "Axyos", "funkzcutes20", "Elpha99", "thabummy", "Gend1x69", "Repra RuyOHD", "Tineas Messuous", "SagaFless", "NipplesDevnooon", "datt fire", "YaveCripstwide", "electrur", "TiCodolibal", "ceniColomentin", "LecuranUnMexo", "umskv", "timp", "Nello", "nokobau", "Frosty Parted", "counter4laf", "reguyy500", "MCKOrnnia", "skomone32", "PlockstormSheep", "S00ban", "iskrawn01", "WTc71129", "jebbberb", "neotherdrawof", "yooohbeatan", "fuckb", "johneysell", "Halltyphold", "Metrikkk", "EMem14", "SM3Weirs", "bb2kton", "dust daubo21", "ihaisit", "Grann9993", "probsterimis nife", "GHITORU", "neldmandaway", "YoeeeProltheconnigato", "RebelOpsToAND", "PMME UME", "orymazora", "ScapMades", "190snight0", "scisome", "ribelyfres", "solatig", "timelainowshoke", "tikelsgoot", "IsscaBen", "inculqeano", "Prodong", "amponcer17", "KnackWilly", "wringpoles", "RolsecSoastBoy", "Kalkon", "Tanophero", "Rain23eekSeven", "nyingknighta", "gammerange12", "AnonymistahPrince", "OPZOT89", "Hylloxx", "Wafkykinsober", "MT04", "redafripple", "S3icatsGigate", "freepand99", "Grashday", "missu azulawolf", "YourPoraCole667", "UdeoWoodMigemon", "TheRoochy", "azzzia", "ShackleThisLo89", "idont", "neidjiff", "WO94L", "tagey12", "Ragic0s", "J103453", "my3c Zass", "sirmghgun", "Toodz4019", "the greeskill", "MBB", "unyexdneekey", "bluntage", "sfhsyctorn", "CoastJay", "Dootsfarty", "alexano1219", "ien defaves", "theProz", "sinbull", "fillingcreost", "bragopquachry", "Jazal0000", "darkchance", "N B Boob", "xatman987s", "JustBZ", "blubesgoundi", "defficepluths", "floudy", "Kjayare", "the evyforration", "Nomon", "tolencel101", "WolfRoility", "BAODG3SMS1", "No Om Mudion", "robron84", "MrAndrewr22", "ntmac", "kaitekroad", "kd legarsom", "Floriteco77", "tentonpoon", "Miggal Cable Billia", "amerous", "Scruciory", "GlessDarDodyy", "Night", "chemmer", "shuggler", "brahena725", "Invitaploth", "the no bors urranon", "Sevenups", "BenousSocks", "Vuck Sc", "whatofthangg", "myma freadbast", "olden", "BlackMyCat", "ancheek", "Angonic99", "petero9298", "eriaganusBaw", "DerADCaron", "MOG40R", "thenamenamemy", "eciduillibe", "Ivanelander", "VOs1010", "bibster", "unJamRak", "Ineor82", "grodyoura", "Fnayson", "serpango", "jefffinitic", "Slipstrovert", "Cynca qobRover", "NSB", "joo svy", "JaxHaum", "ikoohehon", "alexnicko", "xrooooooo", "Samroj427", "Vogalbowhy quejr", "Joinscootare", "thefunglellehIIGEAWHOSg", "xolbet019", "Thuzwautin", "SfanSome84", "The Reding", "GretsMC", "marvybolled", "CuntaonousONF", "AborsingLine", "noncurcaka", "Ronrigo", "Silyxarollyhooks", "nikn07", "Danter12323", "guzadi22", "CollRyclord", "Animyindavade", "aleximous", "Yonado 13", "el0xian", "wildy", "chill300", "phugofhewnbrowpopgaster", "Icee Canutin", "powentattnat", "kittkiesploxe", "vulparthons", "Irchreete", "Choogehd", "DestricDrugzer", "hackey11312", "Captain", "cjcras", "pora cogs", "googwismciscuatak", "bigeouru14", "NolatartsBottle", "csstningophuh", "Hyvtas1999", "ZeroAutank", "Mr Pooknagn", "jcgmanl2", "FattyKillDad", "Bubbenmaagoo", "Ak3302zhult", "Cokbertaker", "BillBuckTrole", "Phutidnuck Fon", "firveterTostyMore8999", "frontiploll", "chelsenstrure98", "ruster69", "PleseRuster", "kyler boaghersy", "CaptianYaustpann", "Callsmadfra21d", "PaddyDudbalrjimary", "aremasannicage", "Nakaru Triccirist", "fighwicker", "Londsharker", "jewfless", "vorgosm88", "Giscutes", "Onevilleover", "schuattledobro", "Mattmetrace", "spootzetincho", "mooseeks", "cb4lets", "hapmuthanbannard", "1Pandy", "ACTRobh", "mony  briX47", "Lustaksnames7588", "Methershatz", "EarlyCesteScempLowon", "Sneezer UterExtouline", "neachuarran", "HoodiestAard", "wherrit", "Ecctt", "lateyfield", "Redsar", "MrBoom654", "onix53", "Tacoh6998", "paomb116", "swangni s", "abosmonen", "winker jali", "TheNourTlays7", "MoverphilsInXIrah", "gellyatherewightessuve", "bvansleptons", "thebarbertmacgor", "numiousechumbeep", "alluperriy", "TheReafix34", "bore11703", "drajorjam", "wwmoster", "Marchin2492", "Lams JerkyIsAssamhs", "Pinegensky", "Dynackonitus", "prp", "FasetsNosque", "mikebaninua", "iasykw", "dedoldkrik", "Tamm107", "craptow", "RoZakersJakes", "MuchanGiwa", "rickcuppinbro", "ghaismin", "darkdackmiss", "BostFuckGram", "Skurd", "willflzbey8", "FortheomRebelgus", "BlueRatt", "JastyNou", "jascule", "DiamloustardosAnd", "Inter Heat", "well of raine 3s", "SqV141", "Golf24", "ShitvirusAcYaFrand", "murdysdepherd", "throwawaysac", "drsnam", "MaxyMrDoverthent", "MrTheArgrus", "Anthospheck", "blurojr52", "no3ll", "Sixbubblespunk", "martiangy", "hylcrack", "hustinflifk 2n", "theelasamnwehnnif", "marchiefactgetes620", "Joeper1", "DumbieJarkmatts", "narchin717", "Panmaze terpyro707", "holkmacl11", "Spoonu19", "sissmex", "Whotakicoders lip", "wispamus", "CoMaNiseSomeGuy", "numpcxcoust", "ZelMcNotASmilkrons", "APieFmestuper", "Pacrocurce", "vorkingmegold", "Shsmora", "urnump whitepanta", "36sochiin", "SunquackBlacket", "druterent", "Dictor Hellmejeevern", "iZonon", "farks40411", "hampthenex", "JustyBigbamti1", "DankoLong", "holy syct", "meansactblass", "XadianBterler", "nerdmic", "casab57", "killbij", "athidelko a", "AathicalElefInfless", "8v0egor", "YRFUEShoRo", "SuperWoffeen", "fjlani", "Jeuklebraw", "Paffusit Winad", "Leu169", "seyfurliteo", "Bwillbeow679", "immorkenceing766", "dramoo0", "nfcwarcle13", "Neubla210", "xisd1104", "CaptHomeprace", "DirtyLobsty", "Epicaprottuck", "Ithernable Butt", "Failbropter", "imfanicku", "mether aswork", "axstorm", "dern0l801", "StxL53017", "shadowpoopperq8", "DCFhatz", "harmflack95", "manoftamp", "ctec4lt cuck61", "TirstyTheNewsUne", "OrmonDuu327", "ottlenate villy", "We Heve", "Badjak", "TrinsandSope", "a  lakid", "ponkeraldeein meme", "deckanielkx", "B29thd", "peasernuts512", "ahr1057", "LareaPellowIsPoed", "Williotier", "Homeusme", "DaBloidBodd", "thr2subse", "Dunhma792", "KitterDaybasor", "xoehaero", "jackeyman", "HinderSophes", "mellusdock", "PookeBuddos", "pleepanta", "BroveinHoweOf", "blacal puggin", "black75031", "MrBristyMan", "wc0nnint6u", "BackNeweeu", "Vher ewch42", "Feriqlien", "DrDuvyDovmai", "cobiemb095", "Yatdoat", "Tap Of Dast", "sterricus", "ArixCrodus", "TusleFw", "irizashi3", "LefoVicgust", "Chrischristinuspwords", "Stepifesbick11", "hardongapper", "quatohatpeedle", "Seeyo", "boy pinalta", "vaint", "consisterlamati", "Wheelyfehawkv", "phelsey629", "apstarshats", "illegenaripe", "TurtlesBoyOfLegitMic", "ferigia", "avamistellyhouse", "abulleypleun", "FireniumNumz", "alexxxjunces", "Brginmy", "DistleTimeFlower", "DegoG", "themillmacreddit", "Quinsteactus2", "illman", "TavaVilla", "Afkeringhaps", "NeGinic", "hokee 293", "anzhude", "Feniucastiny", "bubyfutch", "darkmaturapen", "marknew", "somtej7", "Sredkprinta", "aargymikea1712", "TheSchDerton", "jellokp", "yourworksandsoul3", "grimlies", "WareBando", "leaddish", "becobrade", "TheVenibleSRussides", "MC3 0", "cbrukhhrn", "Drapiz", "O da11014", "wigertoon12", "SeroSardai", "ST1130", "Claceballder", "bkwfkx", "Azilux", "stevelockmarklin", "Zauriand275", "yqq905", "smickys", "larieven", "NotIMCrase", "NuckyPuff", "livius", "kreak201j66", "Asiasimean7", "xbob", "Trates Reddit", "goncops", "LaurTheDaInA", "camiarly", "cat space", "Geeee", "ANonsmeEY", "unconettevis 2cimtad", "0n awom", "BasileKeet", "parsettins", "jksl", "thumpsuberkamabig", "DixaganHeil", "DRsMireJusHornson", "RestryRailliber", "plamacejuck89", "BubFace", "evelio", "UronickThans", "waskammanreabe", "MarkColomballs", "Eq Cntect", "gomereeshposter", "Jeneethity1", "TagouseNotS33", "Fryson11987", "grimboecoverder2699", "v8gjess", "SilonBeMoungAtCovans", "DimeyLaizz", "racagentargud", "Beeldang5", "trabligars", "eldreamblans", "Cweccolix", "OS LGOn YourOSPreamy", "L3ERVMRClauch", "snecksidk4tatho", "Dragonvis Bang", "Wolfleklike3", "Ravan8une", "hourschoesconstine", "felfkson", "BranendSurtles", "therephoan", "Locre Muggot", "Yough", "I sevent where", "truswatern", "Tweezer", "kalado217", "cogoffarst", "cuiconfourcat", "gah davel misher", "Ikovanofuar", "relecucal", "wellball13", "SyrofSdividhandz", "villerc15", "HamateCresis", "L3jaMWF", "General", "rossintqred", "bl3epstickant", "MamBee94263", "ThatPharonmakers", "Keugeemovi", "Havr", "GeroriusCan", "MitchPixh3c", "Shepst", "seyyyal", "deffin423", "MDeather", "TravaWorkRankey", "Pineaurous779", "rannaasf", "aurothese", "ohnnnmid", "stuckingthetomestecado", "Musernapfallbill", "rankzn121", "Alperjanoo", "FotAlwyl13", "incomnympreid", "KLL337", "Vines 7 Choose", "mksheel42", "coctoma27", "PhallinHave", "angeracfromial", "MESOSDAGT2R", "LuttyI113", "NillyP4", "nehoodnow", "ierieze", "Saicea", "Rafusern", "ts323", "Ming Scht", "mmedsparten", "Leganis Smarks", "Sol OfHaeds", "omwelvenami", "tockless390", "WhighDittaframPantdo1", "develusinthe", "xx", "dcayskor", "callmectiredway", "Ball SigS", "SurtaTanHab", "chucketinsponker", "Kastheredh", "lathyforvolius", "1 002", "ascel", "daughwyndrs", "Jack Wolf O A", "zroob9", "brizz dright westabee", "gotchloghnarts", "AarloJast", "pik7h023", "NT2CONE", "dbenaterodacalos", "Djon77", "AdiePajes", "NuxFiran", "McZabeem", "AmpernGore", "Rogerpow", "mvnjerk", "0lastbngfXm", "bnandens4", "meejsorke", "sybaiyanschrosalaz", "Steed3Food", "ecooldgiests", "JM517", "gigerssapchill", "cashkeemer", "Tr0ct It third", "tz2citts000", "folofruccing", "Monjcagu", "cdenigrime", "syvenalatt217", "aleccant60016", "MyPrankerPerusfis", "blathbachine", "flyep41", "StoclansFaxUd", "BrienPsqA86", "not dehurs", "Tommymeyiwoon", "thefindne", "Loupannic4len08", "HD25", "TheMarthGovrsky", "wiggiegopotis", "rabb412", "TheNyaftfia", "Whitgling offii", "bllra cool", "ArvalhelGuy", "daingding1973", "Pomechar", "Mongai", "missiemra", "xDxDfox", "adizard tbaon", "heogrollate", "ABL0s2ost", "Sasio", "Galballw", "youroedor", "Shempster", "rkkhj", "anjoha pulada69", "FooFealQuarrewu", "caleandsaured", "2chun2093", "ITMEG152", "rbruce3400", "JawComptin", "Fuckburdersarch", "slostarp", "fulfusetha", "forfali", "peagj", "blacks jmunku", "arother tuigball", "budso", "aFunpot", "msblurdiesxkr0", "ljoney in mal", "toopisug", "ThatBlorrapen", "fark ash98", "jjogee1099", "Shoonmaded", "lomanvivancia", "2namesmanadava", "ATombai", "thrispnessly", "Apixterius", "iamphis222", "Cheekerocks", "Notsekresties", "Hustro16", "ia84", "Happerz0", "welledstarzen", "serabod", "Sanadhear13", "NaughtLo", "prainwavev", "hique1", "twolly14", "T1RScuntystrokaes", "hayuera", "ab5rai308", "MaveDexic84", "crath biber111", "workant rynj", "FMOMSFSAT", "boydrybromer", "Goldersa", "TheSdifsDrm", "introll131", "neonz510", "BagLamnet", "muthywalker", "drinksboggect", "proboomslition", "eric on niwin", "drimeta4908", "MikaShrough", "kncdino400", "tringomutre", "GjoshiBeamt", "GoldensPeeper04", "langas01Frophes", "AmesGal", "YomemBlathR", "ane28", "Spickes I", "Anoulo78", "JAnch", "OnfikinEitintos", "Booty60", "Siding 4 T", "imoneverybuts", "CrondraPuribu", "P394HutChizPrn", "GomphinTV", "Jupist99", "big311", "MetreschoYangonncofskin", "cashpop567", "adam afrunking", "murkeyxworld", "SpeedyBrozatie", "jjmc", "OnOKcom", "Asti Taco", "Serchieto", "Storg", "carchingtah", "rellionicaturn100", "AlikenSandles", "Phoster Chestrax", "gun b", "thommessocuust", "RudenDubsu", "whinewil375", "TheDikekidukilrain", "TotSelBuck", "thebaobowbodgy", "xvOreNugeHewf", "dandastom", "irishgrey", "AbraceHomoStretchbroe", "SteDarLahbell", "TeeRanky", "kekman123", "EpalisPhace482", "speecoespana", "martin thetrd Butt", "Hyekin", "siriousleapy", "Engmorex", "shangeredk65", "HelfSteacyphopeon", "DRLM FLOS", "Tunderpice90", "Reforthesal", "qwackporder", "BuddissClouAmAbay", "ShallsShite", "HoDahghunnet1983", "Higroom", "TRSSHITER", "captainSlips", "Mrspankles"};
	@Override
	public void startUp()
	{
// 		fakeRsn = randomAlphaNumeric(12);

Random generator = new Random();
int randomIndex = generator.nextInt(NAMES.length);
fakeRsn = NAMES[randomIndex];
	}

	@Override
	public void shutDown()
	{
		clientThread.invokeLater(() -> client.runScript(ScriptID.CHAT_PROMPT_INIT));
	}

	@Subscribe
	private void onBeforeRender(BeforeRender event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (config.hideWidgets())
		{
			// do every widget
			for (Widget widgetRoot : client.getWidgetRoots())
			{
				processWidget(widgetRoot);
			}
		}
		else
		{
			// just do the chatbox
			updateChatbox();
		}
	}

	/**
	 * Recursively traverses widgets looking for text containing the players name, replacing it if necessary
	 * @param widget The root widget to process
	 */
	private void processWidget(Widget widget)
	{
		if (widget == null)
		{
			return;
		}

		if (widget.getText() != null)
		{
			widget.setText(replaceRsn(widget.getText()));
		}

		for (Widget child : widget.getStaticChildren())
		{
			processWidget(child);
		}

		for (Widget dynamicChild : widget.getDynamicChildren())
		{
			processWidget(dynamicChild);
		}

		for (Widget nestedChild : widget.getNestedChildren())
		{
			processWidget(nestedChild);
		}
	}

	private void updateChatbox()
	{
		Widget chatboxTypedText = client.getWidget(WidgetInfo.CHATBOX_INPUT);
		if (chatboxTypedText == null || chatboxTypedText.isHidden())
		{
			return;
		}
		String[] chatbox = chatboxTypedText.getText().split(":", 2);

		//noinspection ConstantConditions
		String playerRsn = Text.toJagexName(client.getLocalPlayer().getName());
		if (Text.standardize(chatbox[0]).contains(Text.standardize(playerRsn)))
		{
			chatbox[0] = fakeRsn;
		}

		chatboxTypedText.setText(chatbox[0] + ":" + chatbox[1]);
	}

	@Subscribe
	private void onChatMessage(ChatMessage event)
	{
		//noinspection ConstantConditions
		if (client.getLocalPlayer().getName() == null)
		{
			return;
		}

		String replaced = replaceRsn(event.getMessage());
		event.setMessage(replaced);
		event.getMessageNode().setValue(replaced);

		if (event.getName() == null)
		{
			return;
		}

		boolean isLocalPlayer =
			Text.standardize(event.getName()).equalsIgnoreCase(Text.standardize(client.getLocalPlayer().getName()));

		if (isLocalPlayer)
		{
			event.setName(fakeRsn);
			event.getMessageNode().setName(fakeRsn);
		}
	}

	@Subscribe
	private void onOverheadTextChanged(OverheadTextChanged event)
	{
		event.getActor().setOverheadText(replaceRsn(event.getOverheadText()));
	}

	private String replaceRsn(String textIn)
	{
		//noinspection ConstantConditions
		String playerRsn = Text.toJagexName(client.getLocalPlayer().getName());
		String standardized = Text.standardize(playerRsn);
		while (Text.standardize(textIn).contains(standardized))
		{
			int idx = textIn.replace("\u00A0", " ").toLowerCase().indexOf(playerRsn.toLowerCase());
			int length = playerRsn.length();
			String partOne = textIn.substring(0, idx);
			String partTwo = textIn.substring(idx + length);
			textIn = partOne + fakeRsn + partTwo;
		}
		return textIn;
	}

	private static String randomAlphaNumeric(int count)
	{
		StringBuilder builder = new StringBuilder();
		int i = count;
		while (i-- != 0)
		{
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}
}
