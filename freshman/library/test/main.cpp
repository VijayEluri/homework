#include "mycpptest.h"

#include "System.h"
#include "User.h"
#include "Reader.h"
#include "Admin.h"
#include "Kind.h"
#include "Book.h"
#include "ILibrary.h"
#include "Library.h"

using namespace std;

bool prepareStudents(ILibrary *lib)
{
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createReader("5100309000","secret",Reader::STUDENT));
	assertTrue(admin->createReader("5100309001","secret1",Reader::STUDENT));
	assertTrue(admin->createReader("5100309002","secret2",Reader::STUDENT));
	assertTrue(admin->createReader("5100309003","secret3",Reader::STUDENT));
	assertTrue(admin->createReader("5100309004","secret4",Reader::STUDENT));
	return true;
}

bool prepareTeachers(ILibrary *lib)
{
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createReader("T5100309000","secret",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309001","secret1",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309002","secret2",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309003","secret3",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309004","secret4",Reader::TEACHER));
	return true;
}

bool prepareKinds(ILibrary *lib)
{
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createKind("0-57984654",L"CLRS 算法导论",L" C , L， R ,S, ",L"CS-00-02"));
	assertTrue(admin->createKind("0-57984655",L"TAOCP-1",L"Donald Knuth",L"CS-01-01"));
	assertTrue(admin->createKind("0-57984656",L"The art of computer programming volume 2nd.",L" Donald Knuth, ",L"CS-01-02"));
	assertTrue(admin->createKind("0-57984657",L"程序设计的艺术 = The art of computer programming volume 3rd.",L"  ,Donald Knuth,  ",L"CS-01-03"));
	return true;
}

bool prepareBooks(ILibrary *lib)
{
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);

	std::vector<Kind *> rs=lib->searchByISBN("0-57984654");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984654");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984654");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984654");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984654");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),false));

	rs=lib->searchByISBN("0-57984655");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984655");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984655");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984655");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984655");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),false));

	rs=lib->searchByISBN("0-57984656");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984656");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984656");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984656");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984656");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),false));

	rs=lib->searchByISBN("0-57984657");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984657");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984657");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984657");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),true));
	rs=lib->searchByISBN("0-57984657");
	assertNotEmpty(rs);
	assertTrue(admin->createBook(**rs.begin(),false));

	return true;
}

bool stuBorrowBooks(ILibrary *lib, Reader *reader)
{
	vector<Kind *> rs=lib->searchByISBN("0-57984657");
	assertNotEmpty(rs);
	vector<Book *> rs0=(*rs.begin())->getBooks();
	assertNotEmpty(rs0);
	for(size_t i=0;i<rs0.size();i++)
	{
		if(rs0[i]->isAvailable()){
			assertTrue(reader->borrow(*rs0[i]));
			assertFalse(reader->borrow(*rs0[i]));
			break;
		}
	}
	assertFalse(reader->borrow(**rs0.begin()));
	//searchByAuthor
	//borrow one of each kind of book written by Knuth~
	rs=lib->searchByAuthor(L"Donald Knuth");
	assertNotEmpty(rs);
	for(size_t j=0;j<rs.size();j++)
	{
		rs0=rs[j]->getBooks();
		for(size_t i=0;i<rs0.size();i++)
		{
			if(rs0[i]->isAvailable()){
				assertTrue(reader->borrow(*rs0[i]));
				break;
			}
		}
		assertFalse(reader->borrow(**rs0.begin()));
	}

	return true;
}

BEGIN_TEST_CASE(Normal_Initialization)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Admin_Login)	// depend on Normal_Initialization
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Admin_Username)	// depend on Initial_Admin_Login
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertEquals("admin", admin->getUsername());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Admin_Password)	// depend on Initial_Admin_Login
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertEquals("secret", admin->getPassword());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Search_Nothing_By_ISBN)	// depend on Normal_Initialization
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertEmpty(lib->searchByISBN("9787115239075"));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Get_All_Readers_Should_Return_Nothing)	// depend on Initial_Admin_Login
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertEmpty(admin->getAllReaders());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Only_One_Get_All_Admins)	// depend on Initial_Admin_Login
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertEquals(1, admin->getAllAdmins().size());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Add_Reader_Student_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Del_Reader_Student_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->removeReader("5100309000"));
	assertTrue(admin->removeReader("5100309001"));
	assertTrue(admin->removeReader("5100309002"));
	assertTrue(admin->removeReader("5100309003"));
	assertTrue(admin->removeReader("5100309004"));
	assertFalse(admin->removeReader("5100309000"));
	assertFalse(admin->removeReader("5100309001"));
	assertFalse(admin->removeReader("5100309002"));
	assertFalse(admin->removeReader("5100309003"));
	assertFalse(admin->removeReader("5100309004"));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Add_Reader_Teacher_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Del_Reader_Teacher_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->removeReader("T5100309000"));
	assertTrue(admin->removeReader("T5100309001"));
	assertTrue(admin->removeReader("T5100309002"));
	assertTrue(admin->removeReader("T5100309003"));
	assertTrue(admin->removeReader("T5100309004"));
	assertFalse(admin->removeReader("T5100309000"));
	assertFalse(admin->removeReader("T5100309001"));
	assertFalse(admin->removeReader("T5100309002"));
	assertFalse(admin->removeReader("T5100309003"));
	assertFalse(admin->removeReader("T5100309004"));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Add_Admin_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createAdmin("admin00","secret"));
	assertTrue(admin->createAdmin("admin01","secret1"));
	assertTrue(admin->createAdmin("admin02","secret2"));
	assertTrue(admin->createAdmin("admin03","secret3"));
	assertTrue(admin->createAdmin("admin04","secret4"));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Del_Admin_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createAdmin("admin00","secret"));
	assertTrue(admin->createAdmin("admin01","secret1"));
	assertTrue(admin->createAdmin("admin02","secret2"));
	assertTrue(admin->createAdmin("admin03","secret3"));
	assertTrue(admin->createAdmin("admin04","secret4"));
	assertTrue(admin->removeAdmin("admin00"));
	assertTrue(admin->removeAdmin("admin01"));
	assertTrue(admin->removeAdmin("admin02"));
	assertTrue(admin->removeAdmin("admin03"));
	assertTrue(admin->removeAdmin("admin04"));
	assertFalse(admin->removeAdmin("admin00"));
	assertFalse(admin->removeAdmin("admin01"));
	assertFalse(admin->removeAdmin("admin02"));
	assertFalse(admin->removeAdmin("admin03"));
	assertFalse(admin->removeAdmin("admin04"));
	lib->finalize();
	delete lib;
END_TEST_CASE
	

BEGIN_TEST_CASE(Initial_Student_Login)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Student_Username)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	assertEquals("5100309000", reader->getUsername());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Student_Password)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	assertEquals("secret", reader->getPassword());
	lib->finalize();
	delete lib;
END_TEST_CASE


BEGIN_TEST_CASE(Initial_Teacher_Login)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Teacher_Username)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	assertEquals("T5100309000", reader->getUsername());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Initial_Teacher_Password)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	assertEquals("secret", reader->getPassword());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Get_All_Readers_Init)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	assertTrue(prepareTeachers(lib));
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	std::vector<Reader const *> rlist=admin->getAllReaders();
	std::vector<Reader const *>::iterator itr=rlist.begin();
	for(;itr!=rlist.end();itr++)
	{
		Reader *reader=lib->readerLogin((*itr)->getUsername(),(*itr)->getPassword());
		assertNotNull(reader);
	}
	lib->finalize();
	delete lib;
END_TEST_CASE


BEGIN_TEST_CASE(Add_Kind_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareKinds(lib));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Del_Kind_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareKinds(lib));
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->removeKind("0-57984654"));
	assertTrue(admin->removeKind("0-57984655"));
	assertTrue(admin->removeKind("0-57984656"));
	assertTrue(admin->removeKind("0-57984657"));
	lib->finalize();
	delete lib;
END_TEST_CASE


BEGIN_TEST_CASE(Add_Book_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Reserve_Book_Stu_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	vector<Kind *> rs=lib->searchByISBN("0-57984657");
	vector<Book *> rs0=(*rs.begin())->getBooks();
	assertFalse(reader->reserve(**rs0.begin()));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Borrow_Book_Stu_Normal_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	assertTrue(stuBorrowBooks(lib, reader));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Renew_Book_Stu_Once_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	assertTrue(stuBorrowBooks(lib, reader));
	vector<Book *> rs0=reader->getBorrowedBooks();
	for(size_t i=0;i<rs0.size();i++)
	{
		assertTrue(reader->renew(*rs0[i]));
		assertFalse(reader->renew(*rs0[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Return_Book_Stu_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareStudents(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("5100309000", "secret");
	assertNotNull(reader);
	assertTrue(stuBorrowBooks(lib, reader));
	vector<Book *> rs0=reader->getBorrowedBooks();
	for(size_t i=0;i<rs0.size();i++)
	{
		assertTrue(reader->returnBook(*rs0[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Reserve_Book_Tea_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	vector<Kind *> rs=lib->searchByISBN("0-57984657");
	vector<Book *> rs0=(*rs.begin())->getBooks();
	assertTrue(reader->reserve(**rs0.begin()));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Borrow_Book_Tea_Normal_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	assertTrue(stuBorrowBooks(lib, reader));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Renew_Book_Tea_Once_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	assertTrue(stuBorrowBooks(lib, reader));
	vector<Book *> rs0=reader->getBorrowedBooks();
	for(size_t i=0;i<rs0.size();i++)
	{
		assertTrue(reader->renew(*rs0[i]));
		assertFalse(reader->renew(*rs0[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE


BEGIN_TEST_CASE(Return_Book_Tea_1)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	assertTrue(prepareTeachers(lib));
	assertTrue(prepareKinds(lib));
	assertTrue(prepareBooks(lib));
	Reader *reader = lib->readerLogin("T5100309000", "secret");
	assertNotNull(reader);
	assertTrue(stuBorrowBooks(lib, reader));
	vector<Book *> rs0=reader->getBorrowedBooks();
	for(size_t i=0;i<rs0.size();i++)
	{
		assertTrue(reader->returnBook(*rs0[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

const int DataSize = 10, ExistTestSize = 3, GarbageTestSize = 3;
string isbn_set[1000];
wstring name_set[1000], authors_set[1000], index_set[1000],garbage_set[1000];

inline wstring TA_stow(const string &s)
{
	int len = MultiByteToWideChar(
		GetACP(), 0, s.c_str(), -1,
		NULL, 0
	);
	wchar_t *buf = new wchar_t[len];
	MultiByteToWideChar(
		GetACP(), 0, s.c_str(), -1,
		buf, len
	);
	wstring w(buf);
	delete[] buf;
	return w;
}

void InitializeData()
{
	isbn_set[0] = "9787208095625";
	isbn_set[1] = "9787208095626";
	isbn_set[2] = "9787208095627";
	isbn_set[3] = "9787208095628";
	isbn_set[4] = "9787208095629";
	isbn_set[5] = "9787208095620";
	isbn_set[6] = "9787208095621";
	isbn_set[7] = "9787208095622";
	isbn_set[8] = "9787208095623";
	isbn_set[9] = "9787208095624";

	name_set[0] = L"爱情没那么美好";
	name_set[1] = L"火焰，燃烧吧";
	name_set[2] = L"双面兽事件";
	name_set[3] = L"轮舞";
	name_set[4] = L"巨兽";
	name_set[5] = L"风港";
	name_set[6] = L"浮云";
	name_set[7] = L"无尽世界";
	name_set[8] = L"豆瓣社区";
	name_set[9] = L"这是什么意思";

	authors_set[0] = L"布里吉特·吉罗";
	authors_set[1] = L"约翰·狄克森·卡尔";
	authors_set[2] = L"马杰";
	authors_set[3] = L"高中甫";
	authors_set[4] = L"焦晓菊";
	authors_set[5] = L"许舒";
	authors_set[6] = L"吴菲";
	authors_set[7] = L"胡允桓";
	authors_set[8] = L"新书速递";
	authors_set[9] = L"啥也别说了";

	index_set[0] = L"法国文学(126)";
	index_set[1] = L"小说";
	index_set[2] = L"小说";
	index_set[3] = L"重庆大学出版社";
	index_set[4] = L"上海人民出版社";
	index_set[5] = L"重庆版社";
	index_set[6] = L"复旦大学出版社";
	index_set[7] = L"上海译文出版社";
	index_set[8] = L"快写";
	index_set[9] = L"书评人";


}

BEGIN_TEST_CASE(Insert_And_Remove_Some_Kind_Also_SearchByISBN)	//depend on Initial_Admin_Password & Initial_Admin_Username
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Exist_SearchByName)	// depend on Insert_And_Remove_Some_Kind_Also_SearchByISBN
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < ExistTestSize; i++){
		assertNotEmpty(lib->searchByName(name_set[rand()%DataSize]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Not_Exist_SearchByName)	// depend on Insert_And_Remove_Some_Kind_Also_SearchByISBN
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < GarbageTestSize; i++){
		assertEmpty(lib->searchByName(garbage_set[i]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Exist_SearchByAuthor)	// depend on Insert_And_Remove_Some_Kind_Also_SearchByISBN
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < ExistTestSize; i++){
		assertNotEmpty(lib->searchByAuthor(authors_set[rand()%DataSize]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Not_Exist_SearchByAuthor)	// depend on Insert_And_Remove_Some_Kind_Also_SearchByISBN
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < GarbageTestSize; i++){
		assertEmpty(lib->searchByAuthor(garbage_set[i]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Exist_SearchByIndex)	// depend on Insert_And_Remove_Some_Kind_Also_SearchByISBN
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < ExistTestSize; i++){
		assertNotEmpty(lib->searchByIndex(index_set[rand()%DataSize]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(Not_Exist_SearchByIndex)	// depend on Insert_And_Remove_Some_Kind_Also_SearchByISBN
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	for(int i = 0; i < DataSize; i++){
		admin->createKind(isbn_set[i],name_set[i],authors_set[i],index_set[i]);
		assertNotEmpty(lib->searchByISBN(isbn_set[i]));
	}
	for(int i = 0; i < GarbageTestSize; i++){
		assertEmpty(lib->searchByIndex(garbage_set[i]));
	}
	for(int i = 0; i < DataSize; i++){
		admin->removeKind(isbn_set[i]);
		assertEmpty(lib->searchByISBN(isbn_set[i]));
	}
	lib->finalize();
	delete lib;
END_TEST_CASE

/** NOTE: 以下测试与之前的测试数据基本一致！ **/
BEGIN_TEST_CASE(ADD_REACHER_STUDENT)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createReader("5100309000","secret",Reader::STUDENT));
	assertFalse(admin->createReader("5100309000","secret",Reader::STUDENT));
	assertTrue(admin->createReader("5100309001","secret1",Reader::STUDENT));
	assertFalse(admin->createReader("5100309001","secret1",Reader::STUDENT));
	assertTrue(admin->createReader("5100309002","secret2",Reader::STUDENT));
	assertFalse(admin->createReader("5100309002","secret2",Reader::STUDENT));
	assertTrue(admin->createReader("5100309003","secret3",Reader::STUDENT));
	assertFalse(admin->createReader("5100309003","secret3",Reader::STUDENT));
	assertTrue(admin->createReader("5100309004","secret4",Reader::STUDENT));
	assertFalse(admin->createReader("5100309004","secret4",Reader::STUDENT));
	assertEquals(5,(admin->getAllReaders()).size());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(ADD_READER_TEACHER)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createReader("T5100309000","secret",Reader::TEACHER));
	assertFalse(admin->createReader("T5100309000","secret",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309001","secret1",Reader::TEACHER));
	assertFalse(admin->createReader("T5100309001","secret1",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309002","secret2",Reader::TEACHER));
	assertFalse(admin->createReader("T5100309002","secret2",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309003","secret3",Reader::TEACHER));
	assertFalse(admin->createReader("T5100309003","secret3",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309004","secret4",Reader::TEACHER));
	assertFalse(admin->createReader("T5100309004","secret4",Reader::TEACHER));
	assertEquals(5,(admin->getAllReaders()).size());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(DEL_READER_STUDENT)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
    assertTrue(admin->createReader("5100309000","secret",Reader::STUDENT));
    assertFalse(admin->createReader("5100309000","secret",Reader::STUDENT));
    assertTrue(admin->createReader("5100309001","secret",Reader::STUDENT));
    assertFalse(admin->createReader("5100309001","secret",Reader::STUDENT));
    assertEquals(2,(admin->getAllReaders()).size());
	assertTrue(admin->removeReader("5100309000"));
	assertTrue(admin->removeReader("5100309001"));
	assertEquals(0,(admin->getAllReaders()).size());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(GET_READER_BY_NAME)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createReader("T5100309000","secret",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309001","secret1",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309002","secret2",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309003","secret3",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309004","secret4",Reader::TEACHER));
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(DEL_READER_TEACHER)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
    //create readers
	assertTrue(admin->createReader("T5100309000","secret",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309001","secret1",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309002","secret2",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309003","secret3",Reader::TEACHER));
	assertTrue(admin->createReader("T5100309004","secret4",Reader::TEACHER));
	assertEquals(5, (admin->getAllReaders()).size());
    //delete success
	assertTrue(admin->removeReader("T5100309000"));
	assertTrue(admin->removeReader("T5100309001"));
	assertTrue(admin->removeReader("T5100309002"));
	assertTrue(admin->removeReader("T5100309003"));
	assertTrue(admin->removeReader("T5100309004"));
	//delete false
	assertFalse(admin->removeReader("T5100309000"));
	assertFalse(admin->removeReader("T5100309001"));
	assertFalse(admin->removeReader("T5100309002"));
	assertFalse(admin->removeReader("T5100309003"));
	assertFalse(admin->removeReader("T5100309004"));
	assertEquals(0, (admin->getAllReaders()).size());

	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(ADD_ADMIN)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createAdmin("admin00","secret"));
	assertTrue(admin->createAdmin("admin01","secret1"));
	assertTrue(admin->createAdmin("admin02","secret2"));
	assertTrue(admin->createAdmin("admin03","secret3"));
	assertTrue(admin->createAdmin("admin04","secret4"));
    assertEquals(6, (admin->getAllAdmins()).size());
	lib->finalize();
	delete lib;
END_TEST_CASE

BEGIN_TEST_CASE(DEL_ADMIN)
	ILibrary *lib = new Library();
	assertNotNull(lib);
	assertTrue(lib->initialize());
	Admin *admin = lib->adminLogin("admin", "secret");
	assertNotNull(admin);
	assertTrue(admin->createAdmin("admin00","secret"));
	assertTrue(admin->createAdmin("admin01","secret1"));
	assertTrue(admin->createAdmin("admin02","secret2"));
	assertTrue(admin->createAdmin("admin03","secret3"));
	assertTrue(admin->createAdmin("admin04","secret4"));
	assertEquals(6,(admin->getAllAdmins()).size());
	assertTrue(admin->removeAdmin("admin00"));
	assertTrue(admin->removeAdmin("admin01"));
	assertTrue(admin->removeAdmin("admin02"));
	assertTrue(admin->removeAdmin("admin03"));
	assertTrue(admin->removeAdmin("admin04"));
	assertFalse(admin->removeAdmin("admin00"));
	assertFalse(admin->removeAdmin("admin01"));
	assertFalse(admin->removeAdmin("admin02"));
	assertFalse(admin->removeAdmin("admin03"));
	assertFalse(admin->removeAdmin("admin04"));
	assertEquals(1,(admin->getAllAdmins()).size());
	lib->finalize();
	delete lib;
END_TEST_CASE
/** ... by Shen Jiajun & He Danyang **/

BEGIN_TEST_SUITE(Test_ACM_Library)
	RUN_TEST_CASE(Normal_Initialization);
	RUN_TEST_CASE(Initial_Admin_Login);
	RUN_TEST_CASE(Initial_Admin_Username);
	RUN_TEST_CASE(Initial_Admin_Password);
	RUN_TEST_CASE(Search_Nothing_By_ISBN);
	RUN_TEST_CASE(Get_All_Readers_Should_Return_Nothing);
	RUN_TEST_CASE(Only_One_Get_All_Admins);

	InitializeData();
	RUN_TEST_CASE(Insert_And_Remove_Some_Kind_Also_SearchByISBN);
	RUN_TEST_CASE(Exist_SearchByName);
	RUN_TEST_CASE(Not_Exist_SearchByName);
	RUN_TEST_CASE(Exist_SearchByAuthor);
	RUN_TEST_CASE(Not_Exist_SearchByAuthor);
	RUN_TEST_CASE(Exist_SearchByIndex);
	RUN_TEST_CASE(Not_Exist_SearchByIndex);

	RUN_TEST_CASE(DEL_ADMIN);
	RUN_TEST_CASE(ADD_ADMIN);
	RUN_TEST_CASE(DEL_READER_TEACHER);
	RUN_TEST_CASE(GET_READER_BY_NAME);
	RUN_TEST_CASE(DEL_READER_STUDENT);
	RUN_TEST_CASE(GET_READER_BY_NAME);
	
	RUN_TEST_CASE(Add_Reader_Student_1);
	RUN_TEST_CASE(Add_Reader_Teacher_1);
	RUN_TEST_CASE(Add_Kind_1);
	RUN_TEST_CASE(Add_Admin_1);
	RUN_TEST_CASE(Add_Book_1);
	RUN_TEST_CASE(Initial_Student_Login);
	RUN_TEST_CASE(Initial_Student_Username);
	RUN_TEST_CASE(Initial_Student_Password);
	RUN_TEST_CASE(Initial_Teacher_Login);
	RUN_TEST_CASE(Initial_Teacher_Username);
	RUN_TEST_CASE(Initial_Teacher_Password);

	RUN_TEST_CASE(Reserve_Book_Stu_1);
	RUN_TEST_CASE(Borrow_Book_Stu_Normal_1);
	RUN_TEST_CASE(Renew_Book_Stu_Once_1);
	RUN_TEST_CASE(Return_Book_Stu_1);
	RUN_TEST_CASE(Borrow_Book_Stu_Normal_1);
	RUN_TEST_CASE(Return_Book_Stu_1);
	RUN_TEST_CASE(Reserve_Book_Tea_1);
	RUN_TEST_CASE(Borrow_Book_Tea_Normal_1);
	RUN_TEST_CASE(Renew_Book_Tea_Once_1);
	RUN_TEST_CASE(Return_Book_Tea_1);

	RUN_TEST_CASE(Get_All_Readers_Init);
	RUN_TEST_CASE(Del_Reader_Student_1);
	RUN_TEST_CASE(Del_Reader_Teacher_1);
	RUN_TEST_CASE(Del_Admin_1);
	RUN_TEST_CASE(Del_Kind_1);
END_TEST_SUITE

int main()
{
	Test_ACM_Library();
	system("pause");
	return 0;
}
_Stu_1);
	RUN_TEST_CASE(Borrow_Book_Stu_Normal_1);
	RUN_TEST_CASE(Return_Book_Stu_1);
	RUN_TEST_CASE(Reserve_Book_Tea_1);
	RUN_TEST_CASE(Borrow_Book_Tea_Normal_1);
	RUN_TEST_CASE(Renew_Book_Tea_Once_1);
	RUN_TEST_CASE(Return_Book_Tea_1);

	RUN_TEST_CASE(Get_All_Readers_Init);
	RUN_TEST_CASE(Del_Reader_Student_1);
	RUN_TEST_CASE(Del_Reader_Teac
