import jenkins.model.*
import hudson.security.*

def j = Jenkins.get()

// 1) Security realm (no public signup)
def realm = new HudsonPrivateSecurityRealm(false)
j.setSecurityRealm(realm)

// 2) Users: admin + least-privileged pipeline user
def adminPwd = System.getenv("JENKINS_ADMIN_PWD") ?: "AdminPass123!"
def botPwd   = System.getenv("JENKINS_CI_BOT_PWD") ?: "BotPass123!"
if (realm.getUser("admin")  == null) { realm.createAccount("admin", adminPwd) }
if (realm.getUser("ci-bot") == null) { realm.createAccount("ci-bot", botPwd) }

// 3) Authorization: disable anonymous; give minimal rights to ci-bot
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, "admin")
strategy.add(Item.READ, "ci-bot")
strategy.add(Item.BUILD, "ci-bot")
strategy.add(View.READ, "ci-bot")
strategy.add(Run.READ, "ci-bot")
j.setAuthorizationStrategy(strategy)   // no anonymous grants ⇒ anonymous disabled

j.save()
