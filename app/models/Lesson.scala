package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class Lesson(id: Long, name: String)

case class Bilet(id: Long, lesson_id: Long)

case class Question(id: Long, bilet_id: Long, typ: Int, text: String, image: String, answer: String)

case class Variant(id: Long, question_id: Long, text: String)

case class Stat(user_id: Long, bilet_id: Long, question_id: Long, right: Int, answer: String)

case class BiletStat(user_id: Long, bilet_id: Long, ra: Int, max: Int)

object Lesson {
  val simple = {
    get[Int]("Lessons.id") ~
      get[String]("Lessons.name") map {
      case id ~ name => Lesson(id, name)
    }
  }

  def find(id: Long): Lesson = {
    DB.withConnection(implicit connection =>
      SQL("select * from Lessons where id={id}").on('id -> id).as(Lesson.simple.single)
    )
  }

  def findByName(name: String): Lesson = {
    DB.withConnection(implicit connection =>
      SQL("select * from Lessons where name={name}").on(
	'name -> name).as(Lesson.simple.single)
		    )
  }

  def findAll: Seq[Lesson] = {
    DB.withConnection { implicit connection =>
      SQL("select * from Lessons order by id").as(Lesson.simple *)
    }
  }
}

object Bilet {
  val simple = {
    get[Int]("Bilets.id") ~
      get[Int]("Bilets.lesson_id") map {
      case id ~ lesson_id => Bilet(id, lesson_id)
    }
  }
  
  def exists(id: Long) = {
    try{
      Bilet.find(id)
      true
    }catch {
     case _: Throwable => false 
    }
  }

  def find(id: Long): Bilet = {
    DB.withConnection(implicit connection =>
      SQL("select * from Bilets where id={id}").on('id -> id).as(Bilet.simple.single)
    )
  }

  def create(lesson_id: Long) = {
    DB.withConnection(implicit connection =>
      SQL("insert into Bilets (lesson_id) VALUES ({lesson_id})").on(
	'lesson_id -> lesson_id
      ).executeUpdate()
    )
  }

  def getLast: Bilet = {
    DB.withConnection(implicit conneciton =>
      SQL("select * from Bilets ORDER BY id DESC LIMIT 1").as(Bilet.simple.single)
		    )
  }

  def inLesson(lesson_id: Long): List[Bilet] = {
    DB.withConnection(implicit connection => 
      SQL("select * from Bilets where lesson_id={lesson_id} order by id").on(
	'lesson_id -> lesson_id 
      ).as(Bilet.simple *)
    )
  }
}

object Question {
  val simple = {
    get[Int]("Questions.id") ~
      get[Int]("Questions.bilet_id") ~
      get[Int]("Questions.typ") ~ 
      get[String]("Questions.text") ~
      get[String]("Questions.image")~
      get[String]("Questions.answer") map {
	case id ~ bilet_id ~ typ ~ text ~ image  ~  answer => Question(id, bilet_id, typ, text, image, answer)
    }
  }

  def create(bilet_id: Long, typ: Int, text: String, image: String, answer: String) = {
    DB.withConnection(implicit connection =>
      SQL("insert into Questions (bilet_id, typ, text, image, answer) VALUES ({bilet_id}, {typ}, {text}, {image}, {answer})").on(
	'bilet_id -> bilet_id,
	'typ -> typ,
	'text -> text,
	'image -> image,
	'answer -> answer
      ).executeUpdate()
		    )
  }

  def getLast: Question = {
    DB.withConnection(implicit conneciton =>
      SQL("select * from Questions ORDER BY id DESC LIMIT 1").as(Question.simple.single)
		    )
  }

  def find(id: Long): Question = {
    DB.withConnection(implicit connection =>
      SQL("select * from Questions where id={id}").on('id -> id).as(Question.simple.single)
    )
  }

  def findByBilet(bilet_id: Long): List[Question] = {
    DB.withConnection(implicit connection =>
      SQL("select * from Questions where bilet_id={bilet_id} order by id").on(
	'bilet_id -> bilet_id
      ).as(Question.simple *)
    )
  }
}

object Variant {
  val simple = {
    get[Int]("Variants.id") ~
      get[Int]("Variants.question_id") ~
      get[String]("Variants.text") map {
      case id ~ question_id ~ text => Variant(id, question_id, text)
    }
  }
  
  def create(question_id: Long, text: String) = {
    DB.withConnection(implicit connection =>
      SQL("insert into Variants (question_id, text) VALUES ({question_id}, {text})").on(
	'question_id -> question_id,
	'text -> text
      ).executeUpdate()
		    )
  }

  def find(id: Long): Variant = {
    DB.withConnection(implicit connection =>
      SQL("select * from Variants where id={id}").on('id -> id).as(Variant.simple.single)
    )
  }

  def findByQuestion(question_id: Long): List[Variant] = {
    DB.withConnection(implicit connection =>
      SQL("select * from Variants where question_id={question_id} order by id").on(
	'question_id -> question_id
      ).as(Variant.simple *)
    )
  }
}

object Stat {
  val simple = {
    get[Long]("Stat.user_id") ~
    get[Long]("Stat.bilet_id") ~
    get[Long]("Stat.question_id") ~
    get[Int]("Stat.right") ~
    get[String]("Stat.answer") map {
      case user_id ~ bilet_id ~ question_id ~ right ~ answer =>
	Stat(user_id, bilet_id, question_id, right, answer)
    }
  }
  
  def newStat(user_id: Long, bilet_id: Long, question_id: Long, right: Int, answer: String) = {
    DB.withConnection(implicit connection =>
      SQL("INSERT INTO Stat (user_id, bilet_id, question_id, right, answer) VALUES ({user_id}, {bilet_id}, {question_id}, {right}, {answer})").on(
	'user_id -> user_id,
	'bilet_id -> bilet_id,
	'question_id -> question_id,
	'right -> right,
	'answer -> answer
      ).executeUpdate()
    )
  }

  def find(user_id: Long, bilet_id: Long) = {
    DB.withConnection(implicit connection => 
      SQL("select * from Stat where user_id={user_id} and bilet_id={bilet_id}").on(
	'user_id -> user_id,
	'bilet_id -> bilet_id
      ).as(Stat.simple *)
    )
  }

  def exists(user_id: Long, bilet_id: Long) = {
    val stat = Stat.find(user_id, bilet_id)
    if(stat.isEmpty) false else true
  }

    def deleteResolve(user_id: Long, bilet_id: Long) = {
      DB.withConnection(implicit connection => 
	SQL("delete from Stat where user_id={user_id} and bilet_id={bilet_id}").on(
	  'user_id -> user_id,
	  'bilet_id -> bilet_id
	).executeUpdate()
      )
      DB.withConnection(implicit connection => 
	SQL("delete from BiletStat where user_id={user_id} and bilet_id={bilet_id}").on(
	  'user_id -> user_id,
	  'bilet_id -> bilet_id
	).executeUpdate()
      )
    }

} 

object BiletStat {
  val simple = {
    get[Long]("BiletStat.user_id") ~ 
    get[Long]("BiletStat.bilet_id") ~ 
    get[Int]("BiletStat.ra") ~
    get[Int]("BiletStat.max") map {
      case user_id ~ bilet_id ~ ra ~ max => BiletStat(user_id , bilet_id , ra, max) 
    }
  }
  
  def newBiletStat(user_id: Long, bilet_id: Long, ra: Int, max: Int) = {
    DB.withConnection(implicit connection =>
      SQL("insert into BiletStat (user_id, bilet_Id, ra, max) VALUES ({user_id}, {bilet_id}, {ra}, {max})").on(
	'user_id -> user_id,
	'bilet_id -> bilet_id,
	'ra -> ra,
	'max -> max
      ).executeUpdate()
    )
  }
  
  def find(user_id: Long, bilet_id: Long): BiletStat = {
   try{
     DB.withConnection(implicit connection =>
       SQL("select * from BiletStat where user_id={user_id} and bilet_id={bilet_id}").on(
	 'user_id -> user_id,
	 'bilet_id -> bilet_id
       ).as(BiletStat.simple.single)
    )} catch{
       case _: Exception => BiletStat(0, 0, 0, 0)
     }
  }

  def solved(user_id: Long, bilet_id: Long):Boolean = {
    var bilStat = BiletStat.find(user_id, bilet_id)
    var max = 1
    if(bilStat.max != 0) max = bilStat.max
    val perc = (bilStat.ra*100)/max
    if(perc > 70) true else false
  }
}
